package co.samsao.reporter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Vincent on 2016-08-10.
 */

public class ListAdapter extends ArrayAdapter<Repository> {

    public ListAdapter(Activity context, List<Repository> repositories){
        super(context, 0, repositories);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        Repository repository = getItem(position);
        View rootView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_repository, parent, false);

        TextView repositoryNameView = (TextView) rootView.findViewById(R.id.list_item_repository_name);
        repositoryNameView.setText(repository.name);

        TextView repositoryLastUpdateDateView = (TextView) rootView.findViewById(R.id.list_item_repository_updateDate);
        repositoryLastUpdateDateView.setText(getContext().getString(R.string.format_LastUpdateDate, repository.lastUpdate));


        return rootView;
    }

}
