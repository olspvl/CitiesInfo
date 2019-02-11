package task.citiesinfo;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.io.IOException;

public class CitiesActivity extends SingleFragmentActivity {


    @Override
    protected Fragment createFragment() {
        return CitiesFragment.newInstance();
    }
}
