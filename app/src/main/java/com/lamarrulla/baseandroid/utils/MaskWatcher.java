package com.lamarrulla.baseandroid.utils;

import android.text.Editable;
import android.text.TextWatcher;

public class MaskWatcher implements TextWatcher {
    private boolean isRuning = false;
    private boolean isDeleting = false;
    private final String mask;

    public MaskWatcher(String mask){
        this.mask = mask;
    }

    public static MaskWatcher buildCpf(){
        return new MaskWatcher("###.###.###-##");
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        isDeleting = count>after;
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if(isRuning||isDeleting){
            return;
        }
        isRuning = true;
        int editableLenght = s.length();
        if(editableLenght<mask.length()){
            if(mask.charAt(editableLenght)!='#'){
                s.append(mask.charAt(editableLenght));
            }else if(mask.charAt(editableLenght-1)!='#'){
                s.insert(editableLenght-1, mask, editableLenght-1, editableLenght);
            }
        }else{
            s.replace(0, editableLenght, s.toString().substring(0, mask.length()));
        }
        isRuning = false;
    }
}
