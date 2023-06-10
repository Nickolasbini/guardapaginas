package com.br.guardapaginas.helpers;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.br.guardapaginas.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.InputMismatchException;
import java.util.List;

public class Functions {
    public static Date now(){
        Date currentTime = Calendar.getInstance().getTime();
        return currentTime;
    }

    public static String ddArray(String[] array){
        String dataToReturn = "";
        for(Integer i = 0; i < array.length; i++){
            dataToReturn += "[";
            dataToReturn += array[i];
            dataToReturn += "]";
        }
        return dataToReturn;
    }

    public static String urlEncode(String val){
        try {
            return URLEncoder.encode(val, String.valueOf(StandardCharsets.UTF_8));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return val.replace(" ", "+");
        }
    }

    public static String ucfirst(String val){
        if (val == null || val.trim().isEmpty()) {
            return val;
        }
        char c[] = val.trim().toLowerCase().toCharArray();
        c[0] = Character.toUpperCase(c[0]);
        return new String(c);
    }

    public static String getNowDate() {
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
        return timeStamp;
    }

    public static String hashMake(String value){
        String generatedPassword = null;
        try{
            // Create MessageDigest instance for MD5
            MessageDigest md = MessageDigest.getInstance("MD5");
            // Add password bytes to digest
            md.update(value.getBytes());
            // Get the hash's bytes
            byte[] bytes = md.digest();
            // This bytes[] has bytes in decimal format. Convert it to hexadecimal format
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < bytes.length; i++) {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            // Get complete hashed password in hex format
            generatedPassword = sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return generatedPassword;
    }

    public static Boolean hashCheck(String hashed, String nonHashed){
        if(!hashed.equals(Functions.hashMake(nonHashed)))
            return false;
        return true;
    }

    public static Integer parseToInteger(String val){
        if(val == null)
            return 0;
        return Integer.parseInt(val);
    }

    public static String parseToString(Integer val){
        if(val == null)
            return "0";
        return String.valueOf(val);
    }

    public static Bitmap parseByteArrayToBitMap(byte[] binary){
        Bitmap result = BitmapFactory.decodeByteArray(binary, 0, binary.length);
        return result;
    }

    public static String formatDate(String dateString) {
        System.out.println("A data enviada: "+dateString);
        if (dateString == null)
            return "";
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
        return format.format(Date.parse(dateString));
    }

    public static String parsePtDateToEn(String ptDate, String separator){
        separator             = (separator == null ? "-" : separator);
        String[] array        = Functions.explode(ptDate, separator);
        String enFormatedDate = array[2] + separator + array[1] + separator + array[0];
        return enFormatedDate;
    }

    public static String parseEnToPt(String enDate){
        String[] array        = Functions.explode(enDate, "-");
        String ptFormatedDate = array[2] + "/" + array[1] + "/" + array[0];
        return ptFormatedDate;
    }

    public static String implode(String[] array, String glue){
        if(array == null || array.length < 1)
            return "";
        if(glue == null)
            glue = "!@!";
        String val    = "";
        Integer total = array.length;
        Integer last  = total - 1;
        for(Integer i = 0; i < total; i++){
            if(i == last){
                val += array[i];
            }else{
                val += array[i] + glue;
            }
        }
        return val;
    }

    public static String implodeListToString(List list, String delimiter){
        delimiter = (delimiter == null ? "," : delimiter);
        return TextUtils.join(delimiter, list);
    }

    public static String[] explode(String value, String glue){
        if(glue == null)
            glue = "!@!";
        String[] array = value.split(glue);
        return array;
    }

    public static String md5(String s) {
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i=0; i<messageDigest.length; i++)
                hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static Boolean isCPFValid(String CPF){
        if (CPF.equals("00000000000") ||
                CPF.equals("11111111111") ||
                CPF.equals("22222222222") || CPF.equals("33333333333") ||
                CPF.equals("44444444444") || CPF.equals("55555555555") ||
                CPF.equals("66666666666") || CPF.equals("77777777777") ||
                CPF.equals("88888888888") || CPF.equals("99999999999") ||
                (CPF.length() != 11))
            return(false);

        char dig10, dig11;
        int sm, i, r, num, peso;

        // "try" - protege o codigo para eventuais erros de conversao de tipo (int)
        try {
            // Calculo do 1o. Digito Verificador
            sm = 0;
            peso = 10;
            for (i=0; i<9; i++) {
                // converte o i-esimo caractere do CPF em um numero:
                // por exemplo, transforma o caractere '0' no inteiro 0
                // (48 eh a posicao de '0' na tabela ASCII)
                num = (int)(CPF.charAt(i) - 48);
                sm = sm + (num * peso);
                peso = peso - 1;
            }

            r = 11 - (sm % 11);
            if ((r == 10) || (r == 11))
                dig10 = '0';
            else dig10 = (char)(r + 48); // converte no respectivo caractere numerico

            // Calculo do 2o. Digito Verificador
            sm = 0;
            peso = 11;
            for(i=0; i<10; i++) {
                num = (int)(CPF.charAt(i) - 48);
                sm = sm + (num * peso);
                peso = peso - 1;
            }

            r = 11 - (sm % 11);
            if ((r == 10) || (r == 11))
                dig11 = '0';
            else dig11 = (char)(r + 48);
            if ((dig10 == CPF.charAt(9)) && (dig11 == CPF.charAt(10)))
                return(true);
            else return(false);
        } catch (InputMismatchException erro) {
            return(false);
        }
    }

    public static void setSystemColors(AppCompatActivity view){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            view.getWindow().setStatusBarColor(view.getResources().getColor(R.color.primary_color, view.getTheme()));
        }else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            view.getWindow().setStatusBarColor(view.getResources().getColor(R.color.primary_color));
        }
        view.getWindow().setNavigationBarColor(view.getResources().getColor(R.color.primary_color));
        ActionBar actionBar;
        actionBar = view.getSupportActionBar();
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#e4b4cc"));
        actionBar.setBackgroundDrawable(colorDrawable);
    }

    public static JSONObject getJSONObjectFromURL(String urlString) throws IOException, JSONException {
        System.out.println("From URL: "+urlString);
        HttpURLConnection urlConnection = null;
        URL url = new URL(urlString);
        System.out.println("Result URL: "+url);
        urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("GET");
        urlConnection.setReadTimeout(10000 /* milliseconds */ );
        urlConnection.setConnectTimeout(15000 /* milliseconds */ );
        urlConnection.setDoOutput(true);
        System.out.println("Vai conectar: "+urlConnection);
        urlConnection.connect();
        System.out.println("Connectado: "+urlConnection);
        BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
        System.out.println("Result: "+br);
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line + "\n");
        }
        br.close();
        String jsonString = sb.toString();
        System.out.println("JSON: " + jsonString);
        return new JSONObject(jsonString);
    }

    public static Date parseStringToDate(String dtStart){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        Date response = null;
        try {
            Date date = format.parse(dtStart);
            response = date;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return response;
    }

    public static String addDaysToDate(Integer daysToAdd, String dateString){
        String response = dateString;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(sdf.parse(dateString));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        c.add(Calendar.DATE, daysToAdd);
        sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date resultdate = new Date(c.getTimeInMillis());
        response = sdf.format(resultdate);
        return response;
    }
}
