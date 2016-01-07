package com.example.photo_frame;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.yandex.disk.rest.Credentials;
import com.yandex.disk.rest.exceptions.http.HttpCodeException;
import com.yandex.disk.rest.exceptions.http.UnauthorizedException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Арсений on 1/6/2016.
 */
public class MyListFragment extends ListFragment implements LoaderManager.LoaderCallbacks<List<ListItem>> {
    private static final String TAG = "MyListFragment";

    private static final String CURRENT_DIR_KEY = "example.current.dir";

    private static final String ROOT = "/";

    private Credentials credentials;
    private String currentDir;

    private ListExampleAdapter adapter;
    private Handler handler;
    public List<ListItem> whatToShow;
    public HashMap<String,String> url_map;
    private Client retrofitClient;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setDefaultEmptyText();

        setHasOptionsMenu(true);


        registerForContextMenu(getListView());

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String username = preferences.getString(TokenActivity.USERNAME, null);
        String token = preferences.getString(TokenActivity.TOKEN, null);

        handler = new Handler();
        credentials = new Credentials(username, token);


        Bundle args = getArguments();
        if (args != null) {
            currentDir = args.getString(CURRENT_DIR_KEY);
        }
        if (currentDir == null) {
            currentDir = ROOT;
        }

        getActivity().getActionBar().setDisplayHomeAsUpEnabled(!ROOT.equals(currentDir));

        adapter = new ListExampleAdapter(getActivity());
        setListAdapter(adapter);
        setListShown(false);
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
     public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.action_bar, menu);
    }


    public void restartLoader() {
        getLoaderManager().restartLoader(0, null, this);
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                getFragmentManager().popBackStack();
                break;
            case R.id.show_images:
                //Show all images from folder
                showAllImages();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void showAllImages(){
        url_map = new HashMap<>();
        for(ListItem i:whatToShow) {
            if (i.getMediaType()!= null && i.getMediaType().equals("image")) {
                url_map.put(i.getName(), i.getPath());
            }
        }

        for(String i:url_map.keySet()){
            String path = url_map.get(i);
            //Not working!!!
            /*
            retrofitClient.sharedInstance().getUrlOfFile(path, new Callback<Response>(){
                @Override
                public void success(Response response, Response response2) {
                    String json = new String(((TypedByteArray) response.getBody()).getBytes());
                    try {
                        JSONObject obj = new JSONObject(json);
                    } catch (Exception e) {

                    }
                }

                @Override
                public void failure(RetrofitError error) {

                }
            });
            */

        }
    }



    @Override
    public Loader<List<ListItem>> onCreateLoader(int i, Bundle bundle) {
        return new ListExampleLoader(getActivity(), credentials, currentDir);
    }


    @Override
    public void onLoadFinished(final Loader<List<ListItem>> loader, List<ListItem> data) {
        if (isResumed()) {
            setListShown(true);
        } else {
            setListShownNoAnimation(true);
        }
        whatToShow = new ArrayList<>(data);
        if (data.isEmpty()) {
            Exception ex = ((ListExampleLoader) loader).getException();
            if (ex != null) {
                handleException(ex);
            } else {
                setDefaultEmptyText();
            }
        } else {
            adapter.setData(data);
        }
    }

    private void handleException(Exception ex) {
        if (ex instanceof HttpCodeException) {
            setEmptyText(((HttpCodeException)ex).getResponse().getDescription());
            if (ex instanceof UnauthorizedException) {
                final MainActivity activity = ((MainActivity) getActivity());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        activity.startFragment();
                    }
                });
            }
        } else {
            setEmptyText(ex.getMessage());
        }
    }

    @Override
    public void onLoaderReset(Loader<List<ListItem>> loader) {
        adapter.setData(null);
    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        ListItem item = (ListItem) getListAdapter().getItem(position);
        Log.d(TAG, "onListItemClick(): " + item);
        if (item.isDir()) {
            changeDir(item.getPath());
        }
    }

    protected void changeDir(String dir) {
        Bundle args = new Bundle();
        args.putString(CURRENT_DIR_KEY, dir);

        MyListFragment fragment = new MyListFragment();
        fragment.setArguments(args);

        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void setDefaultEmptyText() {
        setEmptyText(getString(R.string.no_files));
    }


    public static class ListExampleAdapter extends ArrayAdapter<ListItem> {
        private final LayoutInflater inflater;

        public ListExampleAdapter(Context context) {
            super(context, android.R.layout.simple_list_item_2);
            inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public void setData(List<ListItem> data) {
            clear();
            if (data != null) {
                addAll(data);
            }
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;

            if (convertView == null) {
                view = inflater.inflate(android.R.layout.simple_list_item_2, parent, false);
            } else {
                view = convertView;
            }

            ListItem item = getItem(position);
            ((TextView)view.findViewById(android.R.id.text1)).setText(item.getName());
            ((TextView)view.findViewById(android.R.id.text2)).setText(item.isDir() ? "" : ""+item.getContentLength());

            return view;
        }
    }

}
