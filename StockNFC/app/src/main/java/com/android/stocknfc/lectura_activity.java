package com.android.stocknfc;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.android.stocknfc.datos.ArticulosDatabaseHelper;

import java.io.UnsupportedEncodingException;

/**
 * Created by Desarrollo on 19/06/2017.
 */

public class lectura_activity extends Activity {

    public final int ESCRIBIR_NFC = 2;
    Tag myTag;
    PendingIntent pendingIntent;
    IntentFilter writeTagFilters[];
    Context context = this;
    NfcAdapter nfcAdapter;
    boolean writeMode;
    ArticulosDatabaseHelper bd;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lectura);
        bd = new ArticulosDatabaseHelper(context);

        nfcAdapter = NfcAdapter.getDefaultAdapter(context);
        if (nfcAdapter != null && nfcAdapter.isEnabled()) {
            Toast.makeText(context, context.getString(R.string.nfc_ready), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, context.getString(R.string.nfc_not_ready), Toast.LENGTH_SHORT).show();
            //finish();
        }

        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        tagDetected.addCategory(Intent.CATEGORY_DEFAULT);
        writeTagFilters = new IntentFilter[]{tagDetected};
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_escritura, menu);

        return true;
    }

    public boolean onMenuItemSelected(int featureId, MenuItem item) {

        Intent intent = new Intent(this, escritura_activity.class);
        switch (item.getItemId()) {
            case R.id.menu_escritura:
                //Creamos el Intent para lanzar la Activity EditTravelActivity
                startActivityForResult(intent, ESCRIBIR_NFC);
                break;
            default:
                //onMenuItemSelected se ejecuta siempre por defecto por tanto si es contextual pasará por aqui.
                onContextItemSelected(item);
                break;
        }

        return true;
    }

    protected void onNewIntent(Intent intent){
        if(NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())){
            myTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            Toast.makeText(this, this.getString(R.string.ok_detected), Toast.LENGTH_SHORT).show();
            getNombreArticulo(intent);
        }
    }

    private void getNombreArticulo(Intent intent){
        if (intent.hasExtra(NfcAdapter.EXTRA_TAG)) {
            Parcelable[] parcelables = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            if(parcelables != null && parcelables.length > 0) {
                String nombreArticulo = readTextFromMessage((NdefMessage) parcelables[0]);
                Articulo articulo = bd.obtenerArticulo(bd.getReadableDatabase(), nombreArticulo);
                int stockFinal = decrementarStock(articulo.getStock(), articulo.getId());
                if (nombreArticulo != null) {
                    TextView nombreArticuloTxt = (TextView)findViewById(R.id.nombreProductoLectura);
                    TextView stockArticuloTxt = (TextView)findViewById(R.id.stockProductoLectura);
                    nombreArticuloTxt.setText("Artículo: " + nombreArticulo);
                    stockArticuloTxt.setText("Stock: " + String.valueOf(stockFinal));
                }
            } else {
                Toast.makeText(this, "Etiqueta vacía", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private int decrementarStock(int stockActual, int id){
        int stockFinal = stockActual-1;
        int cant = bd.editArticulo(bd.getWritableDatabase(), stockFinal, id);
        return stockFinal;
    }

    private String readTextFromMessage(NdefMessage ndefMessage) {
        NdefRecord[] ndefRecords = ndefMessage.getRecords();
        if(ndefRecords != null && ndefRecords.length > 0){
            NdefRecord ndefRecord = ndefRecords[0];
            String tagContent = getTextFromNdefRecord(ndefRecord);
            return tagContent;
        }
        return null;
    }

    private String getTextFromNdefRecord(NdefRecord ndefRecord)
    {
        String tagContent = null;
        try {
            byte[] payload = ndefRecord.getPayload();
            String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";
            int languageSize = payload[0] & 0063;
            tagContent = new String(payload, languageSize + 1, payload.length - languageSize - 1, textEncoding);
        } catch (UnsupportedEncodingException e) {
            Log.e("getTextFromNdefRecord", e.getMessage(), e);
        }
        return tagContent;
    }

    public void onPause(){
        super.onPause();
        WriteModeOff();
    }
    public void onResume(){
        super.onResume();
        WriteModeOn();
    }

    private void WriteModeOn(){
        writeMode = true;
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, writeTagFilters, null);
    }

    private void WriteModeOff(){
        writeMode = false;
        nfcAdapter.disableForegroundDispatch(this);
    }
}
