package com.hashmac.recipesapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hashmac.recipesapp.databinding.ActivitySingUpBinding;
import com.hashmac.recipesapp.models.Category;
import com.hashmac.recipesapp.models.Recipe;
import com.hashmac.recipesapp.models.User;

import java.util.Objects;

/**
 * Our Signup Activity design is a simple activity with a single button, three text fields and a text view.
 * The button is used to signup the user and the text fields are used to enter the user's name, email and password.
 * The text view is used to display Signup Page title
 * The activity is launched when the user clicks the Signup button on the LoginActivity.
 * Instead of designing the Signup Activity from scratch, we will copy the Login Activity design and make the necessary changes.
 * Our design is ready, so we will complete the SignupActivity.java class.
 * our basic code is ready, so let's test our signup activity.
 * it works as expected.
 * in the next video we will connect our app to the Firebase.
 * Welcome Back, in this video we will connect our app to the Firebase.
 * For this, we need to create a new project in the Firebase console.
 * Let's create a new project. There are two ways to connect our app to the Firebase.
 * The first way is to use the Firebase Assistant in Android Studio.
 * The second way is to manually add the Firebase SDK to our app.
 * I will prefer 1st way.
 * Now we need to add dependencies to our app. and make some changes in the build.gradle file.
 * As our app is connected to the Firebase, we can use the Firebase Auth class to create a new user.
 * In next video we will create a login function in the LoginActivity.java class.
 * Thank you for watching this video.
 */

public class SingUpActivity extends AppCompatActivity {
    ActivitySingUpBinding binding;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySingUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.btnSignup.setOnClickListener(view -> signup());
        binding.tvLogin.setOnClickListener(view -> finish());
        /*測試DB操作 20241207 1930
        binding.btnTest.setOnClickListener(view -> testControl());
        binding.tvLogin.setOnClickListener(view -> finish());*/
    }
    //測試 增刪改查 20241207 1930 start
    private void testControl() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("test");
        //myRef.setValue("Hello, World!");
        addCategories();
        addRecipes();
    }
    private void addCategories() {
        DatabaseReference categoriesRef = FirebaseDatabase.getInstance().getReference("Categories");

        categoriesRef.child("1").setValue(new Category("1", "https://f4.ftcdn.net/jpg/03/83/42/07/360_F_383420760_eLY1AXaZ5nr9ql7zcTG89k82k60qUcez.jpg", "Pakistani"));
        categoriesRef.child("2").setValue(new Category("2", "https://www.blueosa.com/wp-content/uploads/2020/01/the-best-top-10-indian-dishes.jpg", "Indian"));
        categoriesRef.child("3").setValue(new Category("3", "https://media-cdn.tripadvisor.com/media/photo-s/0a/43/b4/fd/20160208-155331-largejpg.jpg", "Chinese"));
    }
    private void addRecipes() {
        DatabaseReference recipesRef = FirebaseDatabase.getInstance().getReference("Recipes");

        recipesRef.child("-NU3KPhNQ3j3533Di8r4").setValue(new Recipe(
                "400",
                "Indian",
                "Chicken Tikka Masala is a popular North Indian dish made with marinated chicken pieces cooked in a creamy tomato-based sauce. The sauce is infused with a blend of flavorful spices such as cumin, coriander, and garam masala. The dish is typically served with naan bread or steamed rice. To prepare, the chicken is marinated in yogurt and spices before being grilled or baked until tender. The sauce is then prepared separately and the cooked chicken is added to it before being simmered together for a few minutes.",
                "-NU3KPhNQ3j3533Di8r4",
                "https://i1.wp.com/www.thegourmetgourmand.com/wp-content/uploads/2015/03/2-Bowls-of-Chicken-Tikka-Masala.jpg",
                "Chicken Tikka Masala",
                "1 hour"
        ));

        recipesRef.child("-NU3KPhNQ3j3533Di8r2").setValue(new Recipe(
                "300",
                "Indian",
                "Palak Paneer is a vegetarian dish made with fresh spinach and cubes of paneer (Indian cottage cheese) cooked in a fragrant blend of spices such as cumin, coriander, and garam masala. The dish is typically served with naan bread or steamed rice. To prepare, the spinach is blanched and then pureed before being cooked with onions, garlic, and ginger. The paneer is then added to the spinach mixture and simmered until it is tender and infused with the flavors of the spices.",
                "-NU3KPhNQ3j3533Di8r2",
                "https://healthynibblesandbits.com/wp-content/uploads/2020/01/Palak-Paneer-2.jpg",
                "Palak Paneer",
                "45 minutes"
        ));
    }
    //測試 增刪改查 20241207 1930 start

    private void signup() {
        String name = Objects.requireNonNull(binding.etName.getText()).toString().trim();
        String email = Objects.requireNonNull(binding.etEmail.getText()).toString().trim();
        String password = Objects.requireNonNull(binding.etPassword.getText()).toString().trim();
        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter your name, email and password", Toast.LENGTH_SHORT).show();
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
        } else if (password.length() < 6) {
            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
        } else {
            // let's create a new user in the Firebase
            createNewUser(name, email, password);
        }
    }

    private void createNewUser(String name, String email, String password) {
        // Currently, we are not using name field, but we will use it in the next video.
        // So, let's create a new user in the Firebase
        // We will use the Firebase Auth class to create a new user
        // We will use the createUserWithEmailAndPassword() method to create a new user
        // This method takes two parameters, email and password

        // Let's test our signup activity
        // We need to enable the Email/Password sign-in method in the Firebase console
        // Our code works as expected
        // Need to show a progress dialog while creating a new user

        dialog = new ProgressDialog(this);
        dialog.setMessage("Creating user...");
        dialog.setCancelable(false);
        dialog.show();

        FirebaseApp.initializeApp(this);
        FirebaseAuth auth = FirebaseAuth.getInstance();

        FirebaseApp app = FirebaseApp.getInstance();
        if (app != null) {
            Log.d("FirebaseApp", "Name: " + app.getName());
            Log.d("FirebaseApp", "Options: " + app.getOptions().toString());
            Log.d("FirebaseApp", "Is Default: " + app.isDefaultApp());
        } else {
            Log.e("FirebaseApp", "FirebaseApp is not initialized!");
        }

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // user account created successfully
                        saveName(name, email);
                    } else {
                        // account creation failed
                        dialog.dismiss();
                        Toast.makeText(this, "Account creation failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveName(String name, String email) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        User user = new User(FirebaseAuth.getInstance().getUid(), name, email, "", "");
        //確認物件UserInfo
        Log.d("DEBU saveName UserInfo:", "Name: " + user.getName());
        //確認是否登陸FirebaseAuth_getUid
        Log.d("DE FirebaseAuth_getUid:", "Name: " + FirebaseAuth.getInstance().getUid());
        reference.child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid())).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isComplete()) {
                    dialog.dismiss();
                    Toast.makeText(SingUpActivity.this, "User created successfully", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(SingUpActivity.this, MainActivity.class));
                    finishAffinity();
                } else {
                    dialog.dismiss();
                    Toast.makeText(SingUpActivity.this, "Failed to create user", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}