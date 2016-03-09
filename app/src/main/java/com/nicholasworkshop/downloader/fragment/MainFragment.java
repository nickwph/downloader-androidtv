package com.nicholasworkshop.downloader.fragment;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.TextView;

import com.nicholasworkshop.downloader.BuildConfig;
import com.nicholasworkshop.downloader.MainApplication;
import com.nicholasworkshop.downloader.R;
import com.nicholasworkshop.downloader.tool.FileDownloadManager;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import timber.log.Timber;


/**
 * Created by nickwph on 3/5/16.
 */
public class MainFragment extends Fragment {

    @Inject OkHttpClient okHttpClient;
    @Inject Handler handler;
    @Inject FileDownloadManager fileDownloadManager;
    @Inject @Named("TravelingTypewriter") Typeface travelingTypewriterTypeface;

    @Bind(R.id.title) TextView titleView;
    @Bind(R.id.link_input) EditText linkInputView;
    @Bind(R.id.webview) WebView webView;
    @Bind(R.id.webview_url) TextView webViewUrlView;
    @Bind(R.id.webview_container) View webViewContainerView;
    @Bind(R.id.status) TextView statusView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((MainApplication) getActivity().getApplication()).getComponent().inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ButterKnife.bind(this, view);
        titleView.setTypeface(travelingTypewriterTypeface);
        linkInputView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
                Timber.v("actionId=" + actionId + " event=" + event);
                if (actionId == EditorInfo.IME_NULL && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    onGoButtonClicked();
                    return true;
                }
                return false;
            }
        });
    }

    @OnClick(R.id.go)
    @SuppressLint("SetJavaScriptEnabled")
    void onGoButtonClicked() {
        final Handler handler = new Handler();
        String url = getValidatedUrl();
        final WebViewDownloadListener listener = new WebViewDownloadListener();
        webViewContainerView.setVisibility(View.VISIBLE);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setDownloadListener(listener);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                Timber.e("shouldInterceptRequest request=" + request);
                final String url = request.getUrl().toString();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        webViewUrlView.setText(url);
                    }
                });
                try {
                    Request.Builder builder = new Request.Builder();
                    String cookie = CookieManager.getInstance().getCookie(url);
                    if (cookie != null) {
                        builder.addHeader("Cookie", CookieManager.getInstance().getCookie(url));
                    }
                    Call call = okHttpClient.newCall(builder.url(url).head().build());
                    Response response = call.execute();
                    Timber.e("" + response);
                    String contentDisposition = response.header("Content-Disposition");
                    String contentType = response.header("Content-Type");
                    boolean hasAttachment = contentDisposition != null && contentDisposition.contains("attachment");
                    boolean isOctetStream = contentType != null && contentType.equals("application/octet-stream");
                    if (hasAttachment || isOctetStream) {
                        String userAgent = response.header("User-Agent");
                        long contentLength = Long.parseLong(response.header("Content-Length"));
                        listener.onDownloadStart(url, userAgent, contentDisposition, contentType, contentLength);
                        return null;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return super.shouldInterceptRequest(view, request);
            }
        });
        webView.loadUrl(url);
        webView.requestFocus();
    }

    private String getValidatedUrl() {
        String url = linkInputView.getText().toString();
        if (!url.contains("://")) {
            url = "http://" + url;
        }
        return url;
    }

    public boolean onBackPressed() {
        if (webViewContainerView.getVisibility() == View.VISIBLE) {
            webViewContainerView.setVisibility(View.GONE);
            return true;
        }
        return false;
    }

    private class WebViewDownloadListener implements DownloadListener {
        @Override
        public void onDownloadStart(String url, String userAgent, String contentDisposition, final String mimeType, long contentLength) {
            Timber.e("url=" + url + " userAgent=" + userAgent + " contentDisposition=" + contentDisposition + " mimetype=" + mimeType + " contentLength=" + contentLength);
            String filename = null;
            Matcher matcher = Pattern.compile("filename=\"(.*?)\"").matcher(contentDisposition);
            if (matcher.find() && matcher.groupCount() > 0) {
                filename = matcher.group(1);
                Timber.e("filename=" + filename);
            }
            fileDownloadManager.start(url, mimeType, filename)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<FileDownloadManager.Progress>() {
                        @Override
                        public void call(FileDownloadManager.Progress progress) {
                            String factionString = String.format("%.2f%%", (float) progress.getSoFarBytes() / progress.getTotalBytes() * 100);
                            statusView.setText("Downloaded " + factionString + " (" + progress.getSoFarBytes() + "/" + progress.getTotalBytes() + ")");
                            if (progress.getTotalBytes() == progress.getSoFarBytes()) {
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                File file = new File(progress.getPath());
                                file.setReadable(true, false);
                                intent.setDataAndType(Uri.fromFile(file), mimeType);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }

                        }
                    });
            handler.post(new Runnable() {
                @Override
                public void run() {
                    webViewContainerView.setVisibility(View.GONE);
                }
            });
        }
    }
}
