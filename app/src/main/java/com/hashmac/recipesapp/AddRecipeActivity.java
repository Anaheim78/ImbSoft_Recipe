package com.hashmac.recipesapp;

import static java.lang.System.currentTimeMillis;
import android.app.AlertDialog;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hashmac.recipesapp.databinding.ActivityAddRecipeBinding;
import com.hashmac.recipesapp.models.Category;
import com.hashmac.recipesapp.models.Recipe;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import okhttp3.*;


import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Hello Guys, This is the AddRecipeActivity.java file.
 * In this video we will learn how to add a new recipe to the database.
 * We will complete the AddRecipeActivity.java file.
 * Let's start.
 * 1. We will get Data from the user and validate it.
 * 2. We will validate the data.
 * 3. We will create a Recipe Object.
 * 4. We will pick the image from the gallery.
 * 5. We will upload the image to the firebase storage.
 * 6. We will save the recipe object in the firebase database.
 * 7. We will show a toast message to the user.
 * 8. We will finish the activity.
 * Now we will test our code to see if it is working or not.
 * A lot of errors in our dummy data, we will remove them.
 * There is no category in our Recipe Object, we will add it from firebase.
 * Let's test our code again. Again test
 * And App crashed, we will fix it.
 * Our App working fine. In Our Next Video we will create a Recipe Details Page. and load Recipes in RecyclerView.
 * Thank you for watching this video.
 * See you in the next video.
 * Bye Bye.
 */

public class AddRecipeActivity extends AppCompatActivity {
    private static final String IMGUR_CLIENT_ID = "86594aef2075667";
    ActivityAddRecipeBinding binding;
    private boolean isImageSelected = false;
    private ProgressDialog dialog;
    boolean isEdit;
    String recipeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddRecipeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // 9. Load the categories from the firebase database.
        loadCategories();
        // 20241208 16:17  暫時不使用FireBase storage作為圖床
        binding.btnAddRecipe.setOnClickListener(view -> {
            // 1. We will get Data from the user and validate it.
            getData();
        });
        binding.imgRecipe.setOnClickListener(view -> {
            // 4. We will pick the image from the gallery.
            pickImage();
        });
        //加入觀看資料結構20241205 1958
        // 20241208 16:17 按下confirm跑這邊，直接匿名上傳到Imgur
        binding.btnViewStructure.setOnClickListener(v -> showDatabaseStructure());

        // For Edit Purpose
        isEdit = getIntent().getBooleanExtra("isEdit", false);
        if (isEdit) {
            editRecipe();
        }
    }

    private void editRecipe() {
        Recipe recipe = (Recipe) getIntent().getSerializableExtra("recipe");
        recipeId = recipe.getId();
        isImageSelected = true;
        binding.etRecipeName.setText(recipe.getName());
        binding.etDescription.setText(recipe.getDescription());
        binding.etCookingTime.setText(recipe.getTime());
        binding.etCategory.setText(recipe.getCategory());
        binding.etCalories.setText(recipe.getCalories());
        Glide
                .with(binding.getRoot().getContext())
                .load(recipe.getImage())
                .centerCrop()
                .placeholder(R.drawable.image_placeholder)
                .into(binding.imgRecipe);

        binding.btnAddRecipe.setText("Update Recipe");
    }

    private void loadCategories() {
        List<String> categories = new ArrayList<>();
        // Instead of writing code, we use CHat GPT to generate code.
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, categories);
        binding.etCategory.setAdapter(adapter);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Categories");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.hasChildren()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        categories.add(dataSnapshot.getValue(Category.class).getName());
                    }
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void pickImage() {
        // Instead of writing the code for picking image from the gallery, we will use a library. and copy from other activity.
        PickImageDialog.build(new PickSetup()).show(AddRecipeActivity.this).setOnPickResult(r -> {
            Log.e("ProfileFragment", "onPickResult: " + r.getUri());
            binding.imgRecipe.setImageBitmap(r.getBitmap());
            binding.imgRecipe.setScaleType(ImageView.ScaleType.CENTER_CROP);
            isImageSelected = true;
        }).setOnPickCancel(() -> Toast.makeText(AddRecipeActivity.this, "Cancelled", Toast.LENGTH_SHORT).show());
    }

    private void getData() {
        // Fetch All the data from the user in variables.
        String recipeName = Objects.requireNonNull(binding.etRecipeName.getText()).toString();
        String recipeDescription = Objects.requireNonNull(binding.etDescription.getText()).toString();
        String cookingTime = Objects.requireNonNull(binding.etCookingTime.getText()).toString();
        String recipeCategory = binding.etCategory.getText().toString();
        String calories = Objects.requireNonNull(binding.etCalories.getText()).toString();

        // 2. We will validate the data.
        if (recipeName.isEmpty()) {
            binding.etRecipeName.setError("Please enter Recipe Name");
        } else if (recipeDescription.isEmpty()) {
            binding.etDescription.setError("Please enter Recipe Description");
        } else if (cookingTime.isEmpty()) {
            binding.etCookingTime.setError("Please enter Cooking Time");
        } else if (recipeCategory.isEmpty()) {
            binding.etCategory.setError("Please enter Recipe Category");
        } else if (calories.isEmpty()) {
            binding.etCalories.setError("Please enter Calories");
        } else if (!isImageSelected) {
            Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show();
        } else {
            // 3. We will create a Recipe Object.
            // ID will be auto generated.
            dialog = new ProgressDialog(this);
            dialog.setMessage("Uploading Recipe...");
            dialog.setCancelable(false);
            dialog.show();
            Recipe recipe = new Recipe(recipeName, recipeDescription, cookingTime, recipeCategory, calories, "", FirebaseAuth.getInstance().getUid());
            // We also need to pick image and make sure it is not null.
            // 5. We will upload the image to the firebase storage.
            uploadImage(recipe);
        }


    }

    private String uploadImage(Recipe recipe) {
        final String[] url = {""};
        // We will upload the image to the firebase storage.
        binding.imgRecipe.setDrawingCacheEnabled(true);
        Bitmap bitmap = ((BitmapDrawable) binding.imgRecipe.getDrawable()).getBitmap();
        binding.imgRecipe.setDrawingCacheEnabled(false);
        String id = isEdit ? recipe.getId() : currentTimeMillis() + "";
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference().child("images/" + id + "_recipe.jpg");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        Log.d("DE_uploadImage", "img length: " + data.length);
        UploadTask uploadTask = storageRef.putBytes(data);
        uploadTask.continueWithTask(task -> {
            if (!task.isSuccessful()) {
                Log.d("DE_uploadImage", "UploadNotSuccess " + data.length);
                throw Objects.requireNonNull(task.getException());
            }
            return storageRef.getDownloadUrl();
        }).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Uri downloadUri = task.getResult();
                // We need to save this download url in firebase database
                // So that we can load it in our app
                url[0] = downloadUri.toString();
                Log.d("DE__downloadUri", "url " + downloadUri.toString());
                Toast.makeText(AddRecipeActivity.this, "Image Uploaded Successfully", Toast.LENGTH_SHORT).show();
                saveDataInDataBase(recipe, url[0]);
            } else {
                Toast.makeText(AddRecipeActivity.this, "Error in uploading image", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                Log.e("ProfileFragment", "onComplete: " + Objects.requireNonNull(task.getException()).getMessage());
            }
        });
        return url[0];
    }

    private void saveDataInDataBase(Recipe recipe, String url) {
        recipe.setImage(url);
        // 6. We will save the recipe object in the firebase database.
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Recipes");
        if (isEdit) {
            recipe.setId(recipeId);
            reference.child(recipe.getId()).setValue(recipe).addOnCompleteListener(task -> {
                dialog.dismiss();
                if (task.isSuccessful()) {
                    Toast.makeText(AddRecipeActivity.this, "Recipe Updated Successfully", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(AddRecipeActivity.this, "Error in updating recipe", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            String id = reference.push().getKey();
            recipe.setId(id);
            if (id != null) {
                reference.child(id).setValue(recipe).addOnCompleteListener(task -> {
                    dialog.dismiss();
                    if (task.isSuccessful()) {
                        Toast.makeText(AddRecipeActivity.this, "Recipe Added Successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(AddRecipeActivity.this, "Error in adding recipe", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    private void showDatabaseStructure() {
        //測試Fimgur上傳圖片 sample code
        //取資料
        String recipeName = Objects.requireNonNull(binding.etRecipeName.getText()).toString();
        String recipeDescription = Objects.requireNonNull(binding.etDescription.getText()).toString();
        String cookingTime = Objects.requireNonNull(binding.etCookingTime.getText()).toString();
        String recipeCategory = binding.etCategory.getText().toString();
        String calories = Objects.requireNonNull(binding.etCalories.getText()).toString();

        // 2. We will validate the data.
        if (recipeName.isEmpty()) {
            binding.etRecipeName.setError("Please enter Recipe Name");
        } else if (recipeDescription.isEmpty()) {
            binding.etDescription.setError("Please enter Recipe Description");
        } else if (cookingTime.isEmpty()) {
            binding.etCookingTime.setError("Please enter Cooking Time");
        } else if (recipeCategory.isEmpty()) {
            binding.etCategory.setError("Please enter Recipe Category");
        } else if (calories.isEmpty()) {
            binding.etCalories.setError("Please enter Calories");
        } else if (!isImageSelected) {
            Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show();
        } else {
            // 3. We will create a Recipe Object.
            // ID will be auto generated.
            dialog = new ProgressDialog(this);
            dialog.setMessage("Uploading Recipe...");
            dialog.setCancelable(false);
            dialog.show();
            Recipe recipe = new Recipe(recipeName, recipeDescription, cookingTime, recipeCategory, calories, "", FirebaseAuth.getInstance().getUid());

            // 準備圖片數據
            binding.imgRecipe.setDrawingCacheEnabled(true);
            Bitmap bitmap = ((BitmapDrawable) binding.imgRecipe.getDrawable()).getBitmap();
            binding.imgRecipe.setDrawingCacheEnabled(false);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, baos);
            byte[] imageData = baos.toByteArray();

            // Imgur 上傳
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("image", "recipe_image.jpg",
                            RequestBody.create(imageData, MediaType.parse("image/jpeg")))
                    .build();

            Request request = new Request.Builder()
                    .url("https://api.imgur.com/3/image")
                    .header("Authorization", "Client-ID " + IMGUR_CLIENT_ID)
                    .post(requestBody)
                    .build();

            new OkHttpClient().newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    runOnUiThread(() -> {
                        dialog.dismiss();
                        Toast.makeText(AddRecipeActivity.this, "Upload failed: " + e.getMessage(),
                                Toast.LENGTH_LONG).show();
                    });
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    String imgurUrl = null;
                    try {
                        imgurUrl = new JSONObject(response.body().string())
                                .getJSONObject("data")
                                .getString("link");
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                    Log.d("imgurUrl", "url: " + imgurUrl);
                    String finalImgurUrl = imgurUrl;
                    runOnUiThread(() -> saveDataInDataBase(recipe, finalImgurUrl));
                }
            });
        /* 觀看資料結構 2021208 12:15
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                StringBuilder structure = new StringBuilder();
                int maxRows = 3; // 限制顯示的行數
                for (DataSnapshot node : snapshot.getChildren()) {
                    structure.append("\n表格: ").append(node.getKey()).append("\n");
                    if (node.hasChildren()) {
                        structure.append("欄位與值:\n");
                        int rowCount = 0; // 記錄已處理的行數
                        for (DataSnapshot child : node.getChildren()) {
                            if (rowCount >= maxRows) break; // 如果超過最大行數，停止處理
                            structure.append("  節點ID: ").append(child.getKey()).append("\n");
                            for (DataSnapshot field : child.getChildren()) {
                                structure.append("    - ").append(field.getKey())
                                        .append(": ").append(field.getValue()) // 加入實際值
                                        .append(" (").append(getValueType(field.getValue())).append(")\n");
                            }
                            structure.append("\n");
                            rowCount++;
                        }
                    }
                    structure.append("-------------------\n");
                }

                showStructureDialog(structure.toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AddRecipeActivity.this,
                        "錯誤: " + error.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        }); */
        }

        //加入觀看資料結構 20241205 1959

    }
}