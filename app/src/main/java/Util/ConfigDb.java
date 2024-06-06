package Util;

import com.google.firebase.auth.FirebaseAuth;

public class ConfigDb {


    private static FirebaseAuth auth;

    public static FirebaseAuth autenticacao(){
        if(auth == null){
            auth = FirebaseAuth.getInstance();
        }
        return auth;
    }
}
