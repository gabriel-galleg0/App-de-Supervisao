package Model;

import android.content.Context;

import com.bumptech.glide.Priority;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.Key;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoaderFactory;
import com.bumptech.glide.module.AppGlideModule;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.io.InputStream;
import android.content.Context;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.module.AppGlideModule;
import com.google.firebase.storage.StorageReference;
import java.io.InputStream;

@GlideModule
public class MyAppGlideModule extends AppGlideModule {

    /**
     *
     * @param context An Application {@link android.content.Context}.
     * @param glide The Glide singleton that is in the process of being initialized.
     * @param registry An {@link com.bumptech.glide.Registry} to use to register components.
     */
    @Override
    public void registerComponents(Context context, Glide glide, Registry registry) {
        // Register your ModelLoader here for StorageReference to InputStream
        registry.append(StorageReference.class, InputStream.class, new FirebaseImageLoader.Factory());
    }
}
