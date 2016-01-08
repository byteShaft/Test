package byteshaft.com.webtalk;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONException;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private EditText mUserName;
    private Button checkButton;
    private String username;
    private boolean userNotExist = false;
    public static final String USER_EXIST_URL = ("http://testapp-byteshaft.herokuapp.com/api/users/");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mUserName = (EditText) findViewById(R.id.user_name);
        checkButton = (Button) findViewById(R.id.check_button);
        checkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = mUserName.getText().toString().trim();
                if (TextUtils.isEmpty(username)) {
                    mUserName.setError("please must enter username");
                    return;
                }
                new UserExist().execute(username);
            }
        });
    }

    class UserExist extends AsyncTask<String , String, Integer> {

        @Override
        protected Integer doInBackground(String... params) {
            int status = 0;
            try {
               status= userExist(params[0]);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return status;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            System.out.println(integer);
            System.out.println(integer == 404);
            if (integer == 404){
                Drawable drawable = getResources().getDrawable(R.drawable.checkmark);
                drawable.setBounds(0,0,20,20);
                mUserName.setCompoundDrawables(null, null, drawable, null);
                userNotExist = false;

            }
        }
    }

    private static HttpURLConnection openConnectionForUrl(String targetUrl, String method)
            throws IOException {
        URL url = new URL(targetUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestMethod(method);
        return connection;
    }

    public static int userExist(String username)
            throws IOException, JSONException {
        HttpURLConnection connection =
                openConnectionForUrl(USER_EXIST_URL + username +"/"+ "exists", "GET");
        System.out.println(connection.getResponseCode());
        return connection.getResponseCode();
    }


}
