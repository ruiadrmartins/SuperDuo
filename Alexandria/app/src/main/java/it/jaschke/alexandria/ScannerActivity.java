package it.jaschke.alexandria;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import me.dm7.barcodescanner.zbar.Result;
import me.dm7.barcodescanner.zbar.ZBarScannerView;

/**
 * Adapted from ZBar example https://github.com/dm77/barcodescanner
 */
public class ScannerActivity extends Activity implements ZBarScannerView.ResultHandler {

    public static final String TAG = ScannerActivity.class.getSimpleName();

    public static final String SCANNER_RESULT = "scannerResult";
    public static final String SCANNER_RESULT_TYPE = "scannerResultType";

    private String scannedCode = "";
    private String codeType = "";

    private ZBarScannerView mScannerView;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        mScannerView = new ZBarScannerView(this);    // Programmatically initialize the scanner view
        setContentView(mScannerView);                // Set the scanner view as the content view
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();          // Start camera on resume
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();           // Stop camera on pause
    }

    @Override
    public void handleResult(Result rawResult) {
        // Do something with the result here
        Log.v(TAG, rawResult.getContents()); // Prints scan results
        Log.v(TAG, rawResult.getBarcodeFormat().getName()); // Prints the scan format (qrcode, pdf417 etc.)

        // If you would like to resume scanning, call this method below:
        //mScannerView.resumeCameraPreview(this);

        String ean = rawResult.getContents();
        String format = rawResult.getBarcodeFormat().getName();

        if(format.contains(getString(R.string.isbn)) || format.contains(getString(R.string.ean))) {
            scannedCode = ean;
            codeType = format;
        }

        mScannerView.stopCamera();
        mScannerView.removeAllViews();
        onBackPressed();

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra(SCANNER_RESULT, scannedCode);
        intent.putExtra(SCANNER_RESULT_TYPE, codeType);
        setResult(RESULT_OK, intent);
        super.onBackPressed();
    }
}