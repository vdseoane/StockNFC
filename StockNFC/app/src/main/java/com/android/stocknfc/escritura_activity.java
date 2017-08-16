package com.android.stocknfc;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.stocknfc.datos.ArticulosDatabaseHelper;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class escritura_activity extends Activity {
    public final int LEER_NFC = 1;

    PendingIntent pendingIntent;
    IntentFilter writeTagFilters[];
    boolean writeMode;
    Tag myTag;
    Context context = this;
    NfcAdapter nfcAdapter;
    public ArticulosDatabaseHelper bd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
        inflater.inflate(R.menu.menu_lectura, menu);
        return true;
    }

    public boolean onMenuItemSelected(int featureId, MenuItem item) {

        Intent intent = new Intent(this, lectura_activity.class);
        switch (item.getItemId()) {
            case R.id.menu_lectura:
                //Creamos el Intent para lanzar la Activity EditTravelActivity
                startActivityForResult(intent, LEER_NFC);
                break;
            default:
                //onMenuItemSelected se ejecuta siempre por defecto por tanto si es contextual pasará por aqui.
                onContextItemSelected(item);
                break;
        }

        return true;
    }

    public void onClick(View v) {
        Toast.makeText(this, "Escribiendo pegatina NFC...", Toast.LENGTH_LONG).show();

        try{
            //Si no existe tag al que escribir, mostramos un mensaje de que no existe.
            if(myTag == null){
                Toast.makeText(context, context.getString(R.string.noTag), Toast.LENGTH_LONG).show();
            }else{
                //Llamamos al método write que definimos más adelante donde le pasamos por
                //parámetro el tag que hemos detectado y el mensaje a escribir.
                String nombre = getNombreArticulo();
                int stock = getStockArticulo();
                if((nombre != null) && (!nombre.equals(""))) {
                    write(nombre, myTag);
                    Toast.makeText(context, context.getString(R.string.ok_write), Toast.LENGTH_LONG).show();
                    bd.insertarArticulo(bd.getWritableDatabase(), nombre, stock);
                }
                else
                    Toast.makeText(context, "El atributo nombre no es válido", Toast.LENGTH_LONG).show();
            }
        }catch(IOException e){
            Toast.makeText(context, context.getString(R.string.error_write),Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }catch(FormatException e){
            Toast.makeText(context, context.getString(R.string.error_write), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
    protected void onNewIntent(Intent intent){
        if(NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())){
            myTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            Toast.makeText(this, this.getString(R.string.ok_detected), Toast.LENGTH_SHORT).show();
        }
    }

    private String getNombreArticulo(){
        TextView nombreArticuloTxt = (TextView)findViewById(R.id.nombreProducto);
        String nombreArticulo = nombreArticuloTxt.getText().toString();
        if((nombreArticulo != null) && (!nombreArticulo.equals(""))){
            return nombreArticulo;
        } else return null;
    }

    private int getStockArticulo(){
        TextView stockArticuloTxt = (TextView)findViewById(R.id.stockProducto);
        if(stockArticuloTxt.getText().toString() != null && !stockArticuloTxt.getText().toString().equals("")) {
            int stockArticulo = Integer.parseInt(stockArticuloTxt.getText().toString());
            return stockArticulo;
        }else return 0;
    }

    private void write(String text, Tag tag) throws IOException, FormatException{
        //Creamos un array de elementos NdefRecord. Este Objeto representa un registro del mensaje NDEF
        //Para crear el objeto NdefRecord usamos el método createRecord(String s)
        NdefRecord[] records = {createRecord(text)};
        //NdefMessage encapsula un mensaje Ndef(NFC Data Exchange Format). Estos mensajes están
        //compuestos por varios registros encapsulados por la clase NdefRecord
        NdefMessage message = new NdefMessage(records);
        //Obtenemos una instancia de Ndef del Tag
        Ndef ndef = Ndef.get(tag);
        ndef.connect();
        ndef.writeNdefMessage(message);
        ndef.close();
    }

    private NdefRecord createRecord(String text) throws UnsupportedEncodingException{
        String lang = "us";
        byte[] textBytes = text.getBytes();
        byte[] langBytes = lang.getBytes("US-ASCII");
        int langLength = langBytes.length;
        int textLength = textBytes.length;
        byte[] payLoad = new byte[1 + langLength + textLength];

        payLoad[0] = (byte) langLength;

        System.arraycopy(langBytes, 0, payLoad, 1, langLength);
        System.arraycopy(textBytes, 0, payLoad, 1+langLength, textLength);

        NdefRecord recordNFC = new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, new byte[0], payLoad);

        return recordNFC;

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
