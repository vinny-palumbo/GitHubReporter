package co.samsao.reporter;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }


    public static class PlaceholderFragment extends Fragment {

        private ListAdapter mListAdapter;

        public PlaceholderFragment() {
        }

        @Override
        public void onStart() {
            super.onStart();
            FetchDataTask fetchTask = new FetchDataTask();
            fetchTask.execute();
        }

        @Override
        public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            // The ArrayAdapter will take data from a source and
            // use it to populate the ListView it's attached to.
            mListAdapter = new ListAdapter(
                    getActivity(),
                    new ArrayList<Repository>()
            );

            View rootView = inflater.inflate(R.layout.fragment_main, container, false);

            // Get a reference to the ListView, and attach this adapter to it.
            ListView listView = (ListView) rootView.findViewById(R.id.listview_repositories);
            listView.setAdapter(mListAdapter);

            // setup on click event
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    Repository repository = mListAdapter.getItem(position);

                    // Create Alert Dialog
                    AlertDialog dialog = new AlertDialog.Builder(getContext())
                            .setTitle("Details")
                            .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    // Go back to Repositories list
                                }
                            }).create();
                    // Display the Name, Update Date, Language, Default branch and Forks count of the selected repository
                    View v = inflater.inflate(R.layout.dialog, null);
                    // Name
                    View nameTextView = v.findViewById(R.id.dialog_name_textview);
                    String name = getContext().getString(R.string.format_name, repository.name);
                    ((TextView)nameTextView).setText(name);
                    // Last Update
                    View updateTextView = v.findViewById(R.id.dialog_update_textview);
                    String update = getContext().getString(R.string.format_update, repository.lastUpdate);
                    ((TextView)updateTextView).setText(update);
                    // Language
                    View languageTextView = v.findViewById(R.id.dialog_language_textview);
                    String language = getContext().getString(R.string.format_language, repository.language);
                    ((TextView)languageTextView).setText(language);
                    // Default branch
                    View branchTextView = v.findViewById(R.id.dialog_branch_textview);
                    String branch = getContext().getString(R.string.format_branch, repository.defaultBranch);
                    ((TextView)branchTextView).setText(branch);
                    // Forks count
                    View forksTextView = v.findViewById(R.id.dialog_forksCount_textview);
                    String forks = getContext().getString(R.string.format_forks, repository.forksCount);
                    ((TextView)forksTextView).setText(forks);
                    dialog.setView(v);

                    dialog.show();
                }
            });

            return rootView;
        }

        public class FetchDataTask extends AsyncTask<Void, Void, Repository[]> {

            private final String LOG_TAG = FetchDataTask.class.getSimpleName();

            /**
             * Take the String representing the complete data in JSON Format and
             * pull out the data we need to construct the Strings needed for the wireframes.
             */
            private Repository[] getRepositoriesDataFromJson(String repositoriesJsonStr)
                    throws JSONException {
                // These are the names of the JSON objects that need to be extracted.
                final String DATA = "data";
                final String NAME = "name";
                final String LAST_UPDATE = "updated_at";
                final String LANGUAGE = "language";
                final String BRANCH = "default_branch";
                final String FORKS_COUNT = "forks_count";

                JSONObject repositoriesJson = new JSONObject(repositoriesJsonStr);
                JSONArray dataArray= repositoriesJson.getJSONArray(DATA);

                Repository[] resultRepositories = new Repository[dataArray.length()];
                for(int i = 0; i < dataArray.length(); i++) {
                    String name;
                    String lastUpdate;
                    String language;
                    String defaultBranch;
                    int forksCount;

                    // Get the JSON object representing the repository object
                    JSONObject repositoryObject = dataArray.getJSONObject(i);

                    // Get the name
                    name = repositoryObject.getString(NAME);

                    // Get the last update time
                    lastUpdate = repositoryObject.getString(LAST_UPDATE);
                    // transform "YYYY-MM-DDTHH:MM:SSZ" to "YYYY-MM-DD at HH:MM" format
                    int indexOfT = lastUpdate.indexOf("T");
                    int indexOfZ = lastUpdate.indexOf("Z");
                    lastUpdate = lastUpdate.substring(0,indexOfT) + " at " + lastUpdate.substring(indexOfT + 1,indexOfZ - 3);

                    // Get Language
                    language = repositoryObject.getString(LANGUAGE);

                    // Get Default Branch
                    defaultBranch = repositoryObject.getString(BRANCH);

                    // Get Forks Count
                    forksCount = repositoryObject.getInt(FORKS_COUNT);

                    resultRepositories[i] = new Repository(name, lastUpdate, language, defaultBranch, forksCount);
                }

                for (Repository r : resultRepositories) {
                    Log.v(LOG_TAG, "Repository entry: " + r);
                }
                return resultRepositories;
            }

            @Override
            protected Repository[] doInBackground(Void... params) {
                // These two need to be declared outside the try/catch
                // so that they can be closed in the finally block.
                HttpURLConnection urlConnection = null;
                BufferedReader reader = null;

                // Will contain the raw JSON response as a string.
                String repositoriesJsonStr = null;

                try {
                    // Construct the URL for the query
                    URL url = new URL("https://api.github.com/users/vinny-palumbo/repos?callback=foo");

                    // Create the request to GitHub API, and open the connection
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.connect();

                    // Read the input stream into a String
                    InputStream inputStream = urlConnection.getInputStream();
                    StringBuffer buffer = new StringBuffer();
                    if (inputStream == null) {
                        // Nothing to do.
                        return null;
                    }
                    reader = new BufferedReader(new InputStreamReader(inputStream));

                    String line;
                    while ((line = reader.readLine()) != null) {
                        buffer.append(line + "\n");
                    }

                    if (buffer.length() == 0) {
                        // Stream was empty.  No point in parsing.
                        return null;
                    }
                    repositoriesJsonStr = buffer.toString();
                    // Only take string between the two curly brackets to make it a JSON Object later and parse it
                    int indexFirstCurlyBracket = repositoriesJsonStr.indexOf("{");
                    int indexLastCurlyBracket = repositoriesJsonStr.lastIndexOf("}") + 1;
                    repositoriesJsonStr = repositoriesJsonStr.substring(indexFirstCurlyBracket, indexLastCurlyBracket);
                    Log.v(LOG_TAG, "JSON string: " + repositoriesJsonStr);
                } catch (IOException e) {
                    Log.e(LOG_TAG, "Error ", e);
                    // If the code didn't successfully get the data, there's no point in attemping
                    // to parse it.
                    return null;
                } finally {
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (final IOException e) {
                            Log.e(LOG_TAG, "Error closing stream", e);
                        }
                    }
                }

                try {
                    return getRepositoriesDataFromJson(repositoriesJsonStr);
                } catch (JSONException e) {
                    Log.e(LOG_TAG, e.getMessage(), e);
                    e.printStackTrace();
                }

                // This will only happen if there was an error getting or parsing the data.
                return null;
            }

            @Override
            protected void onPostExecute(Repository[] result) {
                if (result != null) {
                    mListAdapter.clear();
                    for(Repository repository : result) {
                        mListAdapter.add(repository);
                    }
                    // New data is back from the server.
                }
            }

        }

    }

}
