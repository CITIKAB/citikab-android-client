package com.trioangle.gofer.GladePay.apiui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.trioangle.gofer.GladePay.GladePaycardActivity;
import com.trioangle.gofer.R;
import com.trioangle.gofer.utils.CommonMethods;

import org.json.JSONException;
import org.json.JSONObject;

import javax.inject.Inject;

public class AuthUrlWebView extends AppCompatActivity implements View.OnClickListener{

    private WebView webView;
    RelativeLayout help_title;
    AlertDialog mydialog;
    String weburl;
    String userid;
    ProgressDialog dialog;
    final AuthurlSingleton si = AuthurlSingleton.getInstance();
    private static final String TAG = "DebugWebChromeClient";

    private boolean isRedirected;
    public ImageView help_title_back;
    public @Inject
    CommonMethods commonMethods;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth_url);
        commonMethods = new CommonMethods();

        dialog = new ProgressDialog(AuthUrlWebView.this);
        dialog.setMessage("Performing transaction... please wait");
        dialog.setCancelable(false);
        dialog.show();

        Intent x = getIntent();
        weburl = x.getStringExtra("weburl");
     //   weburl="https://demo.checkout.gladepay.com/3ds/sim?amount=50&s3did=GP3DS1571298987";
        System.out.println("webURl" + weburl);

        //Get webview
        webView = (WebView) findViewById(R.id.webView1);
        help_title_back =(ImageView)findViewById(R.id.arrow);
       // commonMethods.rotateArrow(help_title_back,this);
        startWebView(weburl);

        help_title = (RelativeLayout) findViewById(R.id.back);
        help_title.setOnClickListener(this);
    }



    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back: {
                onBackPressed();
            }
            break;
        }
    }

    private void startWebView(String url) {

        webView.setWebViewClient(new WebViewClient() {

            // ProgressDialog progressDialog;

            //If you will not use this method url links are opeen in new brower not in webview
//            public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                System.out.println("Webview url "+url);
//
//                if(url.contains("cancel?s_key=")||url.contains("success?s_key=")||url.contains("pre_accept")) {
//                    webView.setVisibility(View.INVISIBLE);
//                }else{
//                    //Load url in webview
//                    webView.setVisibility(View.VISIBLE);
//                }
//
//                view.loadUrl(url);
//                isRedirected = true;
//                return false;
//            }
            public boolean shouldOverrideUrlLoading(WebView view, String url, WebResourceRequest resourceRequest) {
                isRedirected = true;
               // return super.shouldOverrideUrlLoading(view, url);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    webView.loadUrl(resourceRequest.getUrl().toString());
                }
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                isRedirected = false;
            }

            //Show loader on url load
            public void onLoadResource(WebView view, String url) {
                if (!isRedirected) {
                    if (dialog == null) {
                        dialog = new ProgressDialog(AuthUrlWebView.this);
                        dialog.setCancelable(false);

                        if (!dialog.isShowing()&&!(AuthUrlWebView.this).isFinishing()) {
                            dialog.show();
                        }
                    }else{
                        if (!dialog.isShowing()&&!(AuthUrlWebView.this).isFinishing())  {
                            dialog.show();
                        }
                    }

                }
            }

            public void onPageFinished(WebView view, String url) {

              /*  if(url.contains("cancel?s_key=")||url.contains("success?s_key=")||url.contains("pre_accept")) {
                    webView.setVisibility(View.INVISIBLE);
                }else{*/
                    //Load url in webview
                    webView.setVisibility(View.VISIBLE);
               // }
                webView.loadUrl("javascript:android.showHTML(document.getElementsByTagName('body')[0].innerHTML);");
                //webView.loadUrl("javascript:android.showHTML(document.getElementById('json').innerHTML);");

                try {
                    isRedirected=true;
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }

        });

        // Javascript inabled on webview
        webView.getSettings().setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new MyJavaScriptInterface(this), "android");

        webView.setWebChromeClient(new WebChromeClient(){

            public void onProgressChanged(WebView view, int progress) {
                try {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }

            @Override
            public boolean onConsoleMessage(ConsoleMessage m) {
                Log.d(TAG, m.lineNumber() + ": " + m.message() + " - " + m.sourceId());

                return true;
            }
        });

      /*  if(url.contains("cancel?s_key=")||url.contains("success?s_key=")||url.contains("pre_accept")) {
            System.out.println("finish load Webview url 1  "+url);
            webView.setVisibility(View.INVISIBLE);
        }else{*/
            System.out.println("finload Webview url "+url);
            //Load url in webview
            webView.setVisibility(View.VISIBLE);
      //  }
        webView.loadUrl(url);

    }

    class MyJavaScriptInterface {

        private Context ctx;

        MyJavaScriptInterface(Context ctx) {
            this.ctx = ctx;
        }

        @JavascriptInterface
        public void showHTML(String html) {
            try {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
            System.out.println("HTML" + html);
            JSONObject response = null;
            try {

                response = new JSONObject(html);
                System.out.println("Webviewresponse"+response.toString());

                synchronized (si) {
                    si.setResponse(response);
                    si.notify();
                }
                finish();
             /*   if (response != null) {
                    String statuscode = response.getString("status_code");
                    String statusmessage = response.getString("success_message");

                    // remove_status= remove_jsonobj.getString("status");
                    Log.d("OUTPUT IS", statuscode);
                    Log.d("OUTPUT IS", statusmessage);


                    if (statuscode.matches("1")) {

                        //Toast.makeText(getApplicationContext(),statusmessage, Toast.LENGTH_SHORT).show();
                        clearSavedData();

                        if (statusmessage.matches("Request Booking Send to Host"))
                        {
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.request_book_to_host), Toast.LENGTH_SHORT).show();
                            Intent x = new Intent(getApplicationContext(),HomeActivity.class);
                            x.putExtra("tabsaved",5);
                            x.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(x);
                            finish();
                        }if (statusmessage.matches("Payment Successfully Paid"))
                        {

                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.payment_successfull_paid), Toast.LENGTH_SHORT).show();
                            Intent x = new Intent(getApplicationContext(),HomeActivity.class);
                            x.putExtra("tabsaved",5);
                            x.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(x);
                            finish();
                        }


                        if (statusmessage.matches("Payment has Successfully Completed.")) {

                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.success_completed), Toast.LENGTH_SHORT).show();
                            Intent x = new Intent(getApplicationContext(), HomeActivity.class);
                            x.putExtra("tabsaved", 5);
                            x.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(x);
                            finish();
                        }

                    }else
                    {
                        Toast.makeText(getApplicationContext(),statusmessage, Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    Log.d("My App", response.toString());
                }*/

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Throwable t) {
                Log.e("My App", "Could not parse malformed JSON: \"" + response + "\"");
            }
        }
    }
}
