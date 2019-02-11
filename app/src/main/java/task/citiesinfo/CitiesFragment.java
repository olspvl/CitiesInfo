package task.citiesinfo;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

public class CitiesFragment extends Fragment {

    private static final String TAG = "CitiesFragment";
    private static final String url = "https://raw.githubusercontent.com/David-Haim/CountriesToCitiesJSON/master/countriesToCities.json";
    private static final String geonames = "http://api.geonames.org/wikipediaSearchJSON?q=%s&maxRows=1&username=plokiju";

    private Button mShowCitiesButton;
    private Spinner mCountrySpinner;
    private RecyclerView mRecyclerView;

    private String currentCountry;

    public static CitiesFragment newInstance() {
        return new CitiesFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_cities_list, container, false);
        mShowCitiesButton = v.findViewById(R.id.show_cities);
        mCountrySpinner = v.findViewById(R.id.countries_spinner);
        mRecyclerView = v.findViewById(R.id.cities_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        updateSpinner(getContext());
        mShowCitiesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCountrySpinner.getSelectedItem() != null) {
                    currentCountry = mCountrySpinner.getSelectedItem().toString();
                    updateUI(currentCountry);
                }
            }
        });

        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_cities_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.fetch_data:
                FetchItemsTask fetchItemsTask = new FetchItemsTask();
                fetchItemsTask.execute(url);
                String json;
                while (true) {
                    if (fetchItemsTask.result != null) {
                        json = fetchItemsTask.result + "";
                        fetchItemsTask.cancel(true);
                        break;
                    }
                }
                CityDbCtrl.get(getContext()).fillDb(json);
                updateSpinner(getContext());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateSpinner(Context context) {
        List<String> counties = CityDbCtrl.get(context).getCountries();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, R.layout.spinner_item, counties);
        mCountrySpinner.setAdapter(adapter);
        mCountrySpinner.refreshDrawableState();
    }

    private void updateUI(String county) {
        mRecyclerView.setAdapter(new CitiesListAdapter(CityDbCtrl.get(getContext()).getCities(county)));
    }


    private static class FetchItemsTask extends AsyncTask<String, String, String> {
        String result = null;

        @Override
        protected String doInBackground(String... strings) {
            try {
                result = new CitiesFetcher().getUrlString(strings[0]);
            } catch (IOException e) {
                Log.e(TAG, "Failed to fetch URL: ", e);
            }
            return result;
        }
    }

    private class CitiesListHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        private TextView mNameTextView;

        CitiesListHolder(View itemView) {
            super(itemView);
            mNameTextView = (TextView) itemView;
            mNameTextView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            FetchItemsTask fetchItemsTask = new FetchItemsTask();
            fetchItemsTask.execute(String.format(geonames,
                    mNameTextView.getText().toString().replaceAll("\\s+", "+") +
                    "+" + currentCountry));
            String json;
            while (true) {
                if (fetchItemsTask.result != null) {
                    json = fetchItemsTask.result + "";
                    fetchItemsTask.cancel(true);
                    break;
                }
            }
            String wikiUrl = null;
            try {
                JSONObject jsonObject = new JSONObject(json);
                JSONArray jsonArray = jsonObject.getJSONArray("geonames");
                wikiUrl = jsonArray.getJSONObject(jsonArray.length() - 1).getString("wikipediaUrl");
            } catch (JSONException e) {
                Log.e(TAG, "JSON EXCEPTION #1");
                e.printStackTrace();
            }
            if(wikiUrl != null) {
                wikiUrl = "https://" + wikiUrl ;
                Intent i = CityPageActivity.newIntent(getContext(), Uri.parse(wikiUrl));
                startActivity(i);
            }
        }

        public void bindActivity(String city) {
            mNameTextView.setText(city);
        }
    }

    private class CitiesListAdapter extends RecyclerView.Adapter<CitiesListHolder> {
        private List<String> mCities;

        CitiesListAdapter(List<String> cities) {
            mCities = cities;
        }

        @NonNull
        @Override
        public CitiesListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater
                    .inflate(android.R.layout.simple_list_item_1, parent, false);
            return new CitiesListHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull CitiesListHolder holder, int position) {
            String city = mCities.get(position);
            holder.bindActivity(city);
        }

        @Override
        public int getItemCount() {
            return mCities.size();
        }
    }


}
