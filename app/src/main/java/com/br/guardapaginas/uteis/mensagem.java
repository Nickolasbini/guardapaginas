package com.br.guardapaginas.uteis;

import android.app.AlertDialog;
import android.content.Context;

public class mensagem {

    public static void Alert(Context context, String aviso){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle("Aviso");
        alertDialog.setMessage(aviso);
        alertDialog.setPositiveButton("Ok", null);
        alertDialog.show();
    }
}
