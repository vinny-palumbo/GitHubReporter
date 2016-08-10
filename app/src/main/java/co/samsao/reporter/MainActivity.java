package co.samsao.reporter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

        ArrayAdapter<String> mRepositoriesAdapter;

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

            // Create some dummy data for the ListView.
            String[] data = {
                    "samsao/android_technical_test - 2016-08-08",
                    "samsao/dagger2-demo - 2016-06-05",
                    "samsao/DataProvider - 2016-05-27",
                    "samsao/DateRangeCalendarView - 2016-06-16",
                    "samsao/iOS-test - 2016-06-01",
                    "samsao/java-code-styles - 2016-07-04",
                    "samsao/mastersofcode-android-consummer - 2015-09-27"
            };
            List<String> repositories = new ArrayList<String>(Arrays.asList(data));

            // Now that we have some dummy data, create an ArrayAdapter.
            // The ArrayAdapter will take data from a source (like our dummy data) and
            // use it to populate the ListView it's attached to.
            mRepositoriesAdapter =
                    new ArrayAdapter<String>(
                            getActivity(), // The current context (this activity)
                            R.layout.list_item_repository, // The name of the layout ID.
                            R.id.list_item_repository_textview, // The ID of the textview to populate.
                            repositories);

            View rootView = inflater.inflate(R.layout.fragment_main, container, false);

            // Get a reference to the ListView, and attach this adapter to it.
            ListView listView = (ListView) rootView.findViewById(R.id.listview_repositories);
            listView.setAdapter(mRepositoriesAdapter);

            return rootView;
        }
    }

}
