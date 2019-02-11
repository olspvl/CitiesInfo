package task.citiesinfo;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;

public class CityPageActivity extends SingleFragmentActivity {

    public static Intent newIntent(Context context, Uri wikiPage) {
        Intent i = new Intent(context, CityPageActivity.class);
        i.setData(wikiPage);
        return i;
    }

    @Override
    protected Fragment createFragment() {
        return CityPageFragment.newInstance(getIntent().getData());
    }
}
