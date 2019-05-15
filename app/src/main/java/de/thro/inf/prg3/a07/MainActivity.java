package de.thro.inf.prg3.a07;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;

import de.thro.inf.prg3.a07.api.OpenMensaAPI;
import de.thro.inf.prg3.a07.model.Meal;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
	private ArrayAdapter <Meal> arrayAdapter;
	private OpenMensaAPI openMensaAPI;
	private String today;
	List<Meal> meals;
	CheckBox checkBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Retrofit retrofit = new Retrofit.Builder()
			.addConverterFactory(GsonConverterFactory.create())
			.baseUrl("https://openmensa.org/api/v2/")
			.build();

		openMensaAPI = retrofit.create(OpenMensaAPI.class);

		arrayAdapter = new ArrayAdapter<>(
			MainActivity.this,     // context we're in; typically the activity
			R.layout.meal_entry   // where to find the layout for each item
		);

        //get current date
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
		today = sdf.format(new Date());

		final ListView lv = findViewById(R.id.lstV_recipes);
		lv.setAdapter(arrayAdapter);

		Button btn = (Button) findViewById(R.id.btn_refresh);
		btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				btn_refresh_click();
			}
		});

		checkBox = findViewById(R.id.cBx_vegetarianOnly);
    }

    protected void btn_refresh_click(){
		openMensaAPI.getMeals(today).enqueue(new Callback<List<Meal>>() {
			@Override
			public void onResponse(Call<List<Meal>> call, Response<List<Meal>> response) {
				if(response.isSuccessful()){
					arrayAdapter.clear();
					meals = response.body();

					if(checkBox.isChecked()){
						ArrayList<Meal> vegetarianMeals = new ArrayList<>();
						for(Meal meal:meals){
							if(meal.isVegetarian()) vegetarianMeals.add(meal);
						}
						arrayAdapter.addAll(vegetarianMeals);
					}
					else
						arrayAdapter.addAll(meals);
				}
			}

			@Override
			public void onFailure(Call<List<Meal>> call, Throwable t) {

			}
		});
	}
}
