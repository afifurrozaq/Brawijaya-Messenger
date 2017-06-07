package com.yokoding.afifur.brawijayamessenger.Helper;

import android.app.AlertDialog;
import android.content.Context;

import com.squareup.picasso.Picasso;
import com.yokoding.afifur.brawijayamessenger.R;

import java.util.Random;

/**
 * Created by afifur on 25/05/17.
 */

public class ChatHelper {

    private static Random randomAvatarGenerator = new Random();
    private static final int NUMBER_OF_AVATAR = 3;

    /*Generate an avatar randomly*/
    public static int  generateRandomAvatarForUser(){
        return randomAvatarGenerator.nextInt(NUMBER_OF_AVATAR);
    }

    /*Get avatar id*/

    public static int getDrawableAvatarId(int givenRandomAvatarId){

        switch (givenRandomAvatarId){

            case 0:
                return R.mipmap.ic_launcher_round;
            case 1:
                return R.mipmap.ic_launcher_round;
            case 2:
                return R.mipmap.ic_launcher_round;
            default:
                return R.mipmap.ic_launcher_round;
        }
    }


    public static AlertDialog buildAlertDialog(String title, String message, boolean isCancelable, Context context){

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message)
                .setTitle(title);

        if(isCancelable){
            builder.setPositiveButton(android.R.string.ok, null);
        }else {
            builder.setCancelable(false);
        }
        return builder.create();
    }

}
