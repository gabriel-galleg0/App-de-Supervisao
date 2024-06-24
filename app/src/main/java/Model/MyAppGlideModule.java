package Model;

import android.content.Context;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.StorageReference;
import java.io.InputStream;
import com.bumptech.glide.Glide;
@GlideModule
public class MyAppGlideModule extends AppGlideModule {
    /**
     *Esse texto aqui de baixo no momento que fui documentar o código apareceu n sei oq é exatamente
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