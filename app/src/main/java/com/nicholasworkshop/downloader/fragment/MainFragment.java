package com.nicholasworkshop.downloader.fragment;

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
import android.widget.TextView;

import com.nicholasworkshop.downloader.MainApplication;
import com.nicholasworkshop.downloader.databinding.FragmentMainBinding;
import com.nicholasworkshop.downloader.tool.FileDownloadManager;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;
import javax.inject.Named;

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

    @Inject
    OkHttpClient okHttpClient;
    @Inject
    Handler handler;
    @Inject
    FileDownloadManager fileDownloadManager;
    @Inject
    @Named("TravelingTypewriter")
    Typeface travelingTypewriterTypeface;

    private FragmentMainBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((MainApplication) getActivity().getApplication()).getComponent().inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMainBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        binding.title.setTypeface(travelingTypewriterTypeface);
        binding.linkInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
                Timber.v("actionId=" + actionId + " event=" + event);
                if (actionId == EditorInfo.IME_NULL && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    onGoButtonClickedListener.onClick(null);
                    return true;
                }
                return false;
            }
        });
        binding.go.setOnClickListener(onGoButtonClickedListener);
    }

    private View.OnClickListener onGoButtonClickedListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            final Handler handler = new Handler();
            String url = getValidatedUrl();
            final WebViewDownloadListener listener = new WebViewDownloadListener();
            binding.webviewContainer.setVisibility(View.VISIBLE);
            binding.webview.getSettings().setJavaScriptEnabled(true);
            binding.webview.setDownloadListener(listener);
            binding.webview.setWebViewClient(new WebViewClient() {
                @Override
                public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                    Timber.e("shouldInterceptRequest request=" + request);
                    final String url = request.getUrl().toString();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            binding.webviewUrl.setText(url);
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
                        boolean isVideo = contentType != null && contentType.contains("video/");
                        boolean isAudio = contentType != null && contentType.contains("audio/");
                        boolean isOctetStream = contentType != null && contentType.equals("application/octet-stream");
                        if (hasAttachment || isVideo || isAudio || isOctetStream) {
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
            binding.webview.loadUrl(url);
            binding.webview.requestFocus();
        }
    };

    private String getValidatedUrl() {
        String url = binding.linkInput.getText().toString();
        if (!url.contains("://")) {
            url = "http://" + url;
        }
        return url;
    }

    public boolean onBackPressed() {
        if (binding.webviewContainer.getVisibility() == View.VISIBLE) {
            binding.webviewContainer.setVisibility(View.GONE);
            return true;
        }
        return false;
    }

    private class WebViewDownloadListener implements DownloadListener {
        @Override
        public void onDownloadStart(String url, String userAgent, String contentDisposition, final String mimeType, long contentLength) {
            Timber.e("url=" + url + " userAgent=" + userAgent + " contentDisposition=" + contentDisposition + " mimetype=" + mimeType + " contentLength=" + contentLength);
            String filename = null;
            if (contentDisposition != null) {
                Matcher matcher = Pattern.compile("filename=\"(.*?)\"").matcher(contentDisposition);
                if (matcher.find() && matcher.groupCount() > 0) {
                    filename = matcher.group(1);
                    Timber.e("filename=" + filename);
                }
            }
            fileDownloadManager.start(url, mimeType, filename)
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnError(new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                            binding.status.setText("Error: " + throwable.getMessage());
                        }
                    })
                    .subscribe(new Action1<FileDownloadManager.Progress>() {
                        @Override
                        public void call(FileDownloadManager.Progress progress) {
                            String factionString = String.format("%.2f%%", (float) progress.getSoFarBytes() / progress.getTotalBytes() * 100);
                            binding.status.setText("Downloaded " + factionString + " (" + progress.getSoFarBytes() + "/" + progress.getTotalBytes() + ")");
                            if (progress.getTotalBytes() == progress.getSoFarBytes()) {
                                binding.status.setText("Download Completed");
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
                    binding.webviewContainer.setVisibility(View.GONE);
                }
            });
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
