package com.br.guardapaginas.views;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.br.guardapaginas.R;
import com.br.guardapaginas.classes.Book;
import com.br.guardapaginas.classes.holders.BookAdapter;
import com.br.guardapaginas.classes.holders.BookRecycleViewInterface;
import com.br.guardapaginas.databinding.ActivityMainBinding;
import com.br.guardapaginas.helpers.Functions;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class APIListOfBooks extends AppCompatActivity implements BookRecycleViewInterface {

    ActivityMainBinding binding;
    String apiResultsJSON;
    EditText valToSearchInput;
    Button btnFetch;
    Button btnOpenCamera;
    RecyclerView reclycleView;
    TextView bookChosen;
    Button btnConfirmSelection;
    ImageView btnGoBack;
    Book bookObj;
    List<Book>   apiBooksList;
    List<String> bookTitles;
    ImageView loaderImage;
    TextView loadingLabel;
    TextView noResultLabel;
    LinearLayout borderTop;
    LinearLayout borderBottom;
    APIListOfBooks thisClass;
    Book selectedObj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(R.layout.activity_apilist_of_books);
        thisClass = this;

        Functions.setSystemColors(this);

        valToSearchInput    = (EditText) findViewById(R.id.valToSearchInput);
        btnFetch            = (Button) findViewById(R.id.btnFetch);
        btnOpenCamera       = (Button) findViewById(R.id.btnOpenCamera);
        reclycleView        = (RecyclerView) findViewById(R.id.reclycleView);
        bookChosen          = (TextView) findViewById(R.id.bookChosen);
        btnConfirmSelection = (Button) findViewById(R.id.btnConfirmSelection);
        btnGoBack           = (ImageView) findViewById(R.id.btnGoBack);
        bookObj             = new Book(getApplicationContext());
        apiBooksList        = new ArrayList<>();
        bookTitles          = new ArrayList<>();
        loaderImage         = (ImageView) findViewById(R.id.loaderImage);
        loadingLabel        = (TextView) findViewById(R.id.loadingLabel);
        noResultLabel       = (TextView) findViewById(R.id.noResultLabel2);
        borderTop           = (LinearLayout) findViewById(R.id.borderTop);
        borderBottom        = (LinearLayout) findViewById(R.id.borderBottom);

        bookChosen.setVisibility(View.INVISIBLE);
        borderTop.setVisibility(View.GONE);
        borderBottom.setVisibility(View.GONE);
        openLoader(false);

        Intent intent = getIntent();
        apiResultsJSON = intent.getStringExtra("API_RESULTS_JSON");
        if(apiResultsJSON != null)
            feedListWithJson(apiResultsJSON);

        btnFetch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String valueToUse = valToSearchInput.getText().toString();
                if(valueToUse.equals("")){
                    addMessageToToast("Informe um Código ou Título para buscar");
                    return;
                }
                openLoader(true);
                new APIListOfBooks.ApiRequestTask(thisClass).execute("https://www.googleapis.com/books/v1/volumes?q="+Functions.urlEncode(valueToUse)+"&maxResults=10");
            }
        });

        btnOpenCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCameraForReadingISBN();
            }
        });

        btnGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent();
                setResult(Activity.RESULT_OK, i);
                finishActivity(0);
                finish();
            }
        });

        btnConfirmSelection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent returnIntent = getIntent();
                intent.putExtra("SELECTED_BOOK_FROM_API", selectedObj.parseToJSON());
                setResult(Activity.RESULT_OK, returnIntent);
                finishActivity(29);
                finish();
            }
        });
    }

    public void openLoader(Boolean show){
        if(show){
            loaderImage.setVisibility(View.VISIBLE);
            loadingLabel.setVisibility(View.VISIBLE);
        }else{
            loaderImage.setVisibility(View.GONE);
            loadingLabel.setVisibility(View.GONE);
        }
    }

    public String[] feedListWithJson(String jsonString){
        apiBooksList = new ArrayList<>();
        openLoader(true);
        String[] emptyArray = {};
        try {
            JSONObject obj = new JSONObject(jsonString);
            Integer total  = obj.getInt("totalItems");
            if(total < 1){
                addMessageToToast("Nenhum livro encontrado");
                return emptyArray;
            }
            JSONArray arrayOfJSON = obj.getJSONArray("items");
            for(Integer i = 0; i < arrayOfJSON.length(); i++){
                System.out.println("Nos intens: "+arrayOfJSON.get(i));
                JSONObject newJSON       = new JSONObject(arrayOfJSON.get(i).toString());
                JSONObject volumeInfoObj = newJSON.getJSONObject("volumeInfo");
                bookObj = new Book(getApplicationContext());
                if(volumeInfoObj.has("title")) {
                    bookObj.setTitle(volumeInfoObj.getString("title"));
                }else{
                    continue;
                }
                if(volumeInfoObj.has("publisher"))
                    bookObj.setEditorName(volumeInfoObj.getString("publisher"));
                if(volumeInfoObj.has("publishedDate"))
                    bookObj.setReleaseDate(volumeInfoObj.getString("publishedDate"));
                if(volumeInfoObj.has("description"))
                    bookObj.setSynopsis(volumeInfoObj.getString("description"));
                if(volumeInfoObj.has("language"))
                    bookObj.setBookLanguage(volumeInfoObj.getString("language"));
                if(volumeInfoObj.has("pageCount"))
                    bookObj.setNumberOfPages(volumeInfoObj.getString("pageCount"));
                if(volumeInfoObj.has("categories")) {
                    JSONArray arrayCategories = volumeInfoObj.getJSONArray("categories");
                    List<String> categoriesList = new ArrayList<>();
                    for (Integer s = 0; s < arrayCategories.length(); s++) {
                        categoriesList.add(arrayCategories.get(s).toString());
                    }
                    bookObj.setGenderStringArray(Functions.implodeListToString(categoriesList, "|"));
                }
                if(volumeInfoObj.has("authors")) {
                    JSONArray arrayAuthor = volumeInfoObj.getJSONArray("authors");
                    List<String> authorsList = new ArrayList<>();
                    for (Integer s = 0; s < arrayAuthor.length(); s++) {
                        authorsList.add(arrayAuthor.get(s).toString());
                    }
                    bookObj.setAuthor(Functions.implodeListToString(authorsList, " ,"));
                }
                if(volumeInfoObj.has("imageLinks")){
                    JSONObject imageJSONObj = volumeInfoObj.getJSONObject("imageLinks");
                    String sourceLink       = null;
                    if(imageJSONObj.has("smallThumbnail")){
                        sourceLink = imageJSONObj.getString("smallThumbnail");
                    }else if(imageJSONObj.has("thumbnail")){
                        sourceLink = imageJSONObj.getString("thumbnail");
                    }
                    if(sourceLink != null) {
                        booksWithImages++;
                        new DownloadImageTask(this).execute(sourceLink, i.toString());
                    }
                }
                quantityOfBooks++;
                apiBooksList.add(bookObj);
                bookTitles.add(bookObj.getTitle());
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return emptyArray;
        }
        return emptyArray;
    }

    private void openCameraForReadingISBN() {
        ScanOptions scanOptions = new ScanOptions();
        scanOptions.setPrompt("Volume + liga o flash");
        scanOptions.setBeepEnabled(true);
        scanOptions.setOrientationLocked(true);
        scanOptions.setCaptureActivity(CaptureScren.class);
        barLauncher.launch(scanOptions);
    }

    ActivityResultLauncher<ScanOptions> barLauncher = registerForActivityResult(new ScanContract(), result -> {
        if (result.getContents() != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Resultado");
            builder.setMessage(result.getContents());
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int which) {
                    String codeFound = result.getContents();
                    borderTop.setVisibility(View.VISIBLE);
                    borderBottom.setVisibility(View.VISIBLE);
                    new APIListOfBooks.ApiRequestTask(thisClass).execute("https://www.googleapis.com/books/v1/volumes?q="+codeFound+"&maxResults=10");
                }
            }).show();
        }
    });

    public void addMessageToToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    Integer quantityOfBooks  = 0;
    Integer quantityOfImages = 0;
    Integer booksWithImages  = 0;
    public void feedThumbnailsOfBooks(String[] foundImageArray){
        quantityOfImages++;
        if(foundImageArray == null) {
            if(booksWithImages == quantityOfImages)
                feedRecycleView();
            return;
        }
        byte[]  binary= Base64.decode(foundImageArray[0], Base64.NO_WRAP);
        Integer index = Functions.parseToInteger(foundImageArray[1]);
        Book obj = apiBooksList.get(index);
        obj.setBookCover(binary);
        apiBooksList.set(index, obj);
        if(booksWithImages == quantityOfImages)
            feedRecycleView();
    }

    public void feedRecycleView(){
        bookChosen.setVisibility(View.VISIBLE);
        bookChosen.setText("");
        selectedObj = new Book(getApplicationContext());
        reclycleView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        reclycleView.setAdapter(new BookAdapter(getApplicationContext(), apiBooksList, this));
        openLoader(false);
        if(apiBooksList.size() < 1) {
            noResultLabel.setVisibility(View.VISIBLE);
            borderTop.setVisibility(View.GONE);
            borderBottom.setVisibility(View.GONE);
        }else{
            noResultLabel.setVisibility(View.GONE);
            borderTop.setVisibility(View.VISIBLE);
            borderBottom.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onItemClick(int Position) {
        selectedObj = apiBooksList.get(Position);
        bookChosen.setText(selectedObj.getTitle());
        bookChosen.setVisibility(View.VISIBLE);
        System.out.println(selectedObj.parseToString(selectedObj));
        addMessageToToast("Livro selecionado: "+selectedObj.getTitle());
    }

    private class ApiRequestTask extends AsyncTask<String, Void, String> {

        APIListOfBooks parentCaller;

        public ApiRequestTask(APIListOfBooks parent){
            this.parentCaller = parent;
        }

        @Override
        protected String doInBackground(String... params) {
            String apiUrl = params[0];
            StringBuilder json = new StringBuilder();
            System.out.println("URL: "+apiUrl);
            try {
                URL url = new URL(apiUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        json.append(line);
                    }
                    reader.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return json.toString();
        }

        @Override
        protected void onPostExecute(String json) {
            try {
                parentCaller.feedListWithJson(json);
            } catch (Throwable t) {
                addMessageToToast("Código não encontrado, por favor tente outro código!");
                parentCaller.openLoader(false);
            }
        }
    }

    private class DownloadImageTask extends AsyncTask<String, Void, String[]> {
        APIListOfBooks caller;

        public DownloadImageTask(APIListOfBooks caller) {
            this.caller = caller;
        }

        protected String[] doInBackground(String... urls) {
            if(urls.length < 1){
                return null;
            }
            System.out.println(urls[0]);
            String urldisplay = urls[0].replace("http://", "https://");
            Bitmap mIcon11   = null;
            byte[] byteArray = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                System.out.println("URL para pegar: "+urldisplay);
                System.out.println("result: "+in);
                mIcon11 = BitmapFactory.decodeStream(in);
                System.out.println("BitMap: "+mIcon11);
                if(mIcon11 != null){
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    mIcon11.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byteArray = stream.toByteArray();
                    mIcon11.recycle();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            String[] data = new String[2];
            data[0] = Base64.encodeToString(byteArray, Base64.NO_WRAP);
            data[1] = urls[1];
            return data;
        }

        protected void onPostExecute(String[] result) {
            caller.feedThumbnailsOfBooks(result);
        }
    }
}