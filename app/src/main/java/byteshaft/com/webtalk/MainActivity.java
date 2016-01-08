package byteshaft.com.webtalk;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONException;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private EditText mUserName;
    private String username;
    private final String BASE_URL = "http://testapp-byteshaft.herokuapp.com/";
    private final String USER_EXIST_URL = String.format("%s%s", BASE_URL, "api/users/");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mUserName = (EditText) findViewById(R.id.user_name);
        Button checkButton = (Button) findViewById(R.id.check_button);
        checkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = mUserName.getText().toString().trim();
                if (TextUtils.isEmpty(username)) {
                    mUserName.setError("please must enter username");
                    return;
                }
                new UserExistsTask().execute(username);
            }
        });
    }

    private HttpURLConnection openConnectionForUrl(String targetUrl, String method)
            throws IOException {
        URL url = new URL(targetUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestMethod(method);
        return connection;
    }

    private int userExist(String username) throws IOException, JSONException {
        String endPoint = USER_EXIST_URL + username + "/" + "exists";
        HttpURLConnection connection = openConnectionForUrl(endPoint, "GET");
        return connection.getResponseCode();
    }

    class UserExistsTask extends AsyncTask<String, String, Integer> {

        @Override
        protected Integer doInBackground(String... params) {
            try {
                return userExist(params[0]);
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return 0;
        }

        @Override
        protected void onPostExecute(Integer responseCode) {
            super.onPostExecute(responseCode);
            if (responseCode == HttpURLConnection.HTTP_NOT_FOUND) {
                Drawable drawable = getCheckDrawable();
                drawable.setBounds(0, 0, 20, 20);
                mUserName.setCompoundDrawables(null, null, drawable, null);
            } else if (responseCode == HttpURLConnection.HTTP_OK) {
                mUserName.setError("Choose different username");
            }
        }

        private Drawable getCheckDrawable() {
            Resources resources = getResources();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                return resources.getDrawable(R.drawable.checkmark, getTheme());
            } else {
                return resources.getDrawable(R.drawable.checkmark);
            }
        }
    }
}
