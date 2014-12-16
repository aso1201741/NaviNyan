package com.botty.navinyan;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ViewAdapter extends ArrayAdapter<String>{
	static class ViewHolder {
        TextView labelText;
    }

    private LayoutInflater inflater;
    private int typeId;

    // �R���X�g���N�^
    public ViewAdapter(Context context,int textViewResourceId, ArrayList<String> labelList,int type) {
        super(context,textViewResourceId, labelList);
        typeId = type;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        View view = convertView;

        // View���ė��p���Ă���ꍇ�͐V����View�����Ȃ�
        if (view == null) {
            inflater =  (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.activity_list, null);
            TextView label = (TextView)view.findViewById(R.id.tv);
            holder = new ViewHolder();
            holder.labelText = label;
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        // ����̍s�̃f�[�^���擾
        String str = getItem(position);

        if (!TextUtils.isEmpty(str)) {
            // �e�L�X�g�r���[�Ƀ��x�����Z�b�g
            holder.labelText.setText(str);
        }
        
        switch(typeId){
        case 1:
        	if(position%2==0){
                holder.labelText.setBackgroundColor(Color.parseColor("#1E90FF"));
            }else{
                holder.labelText.setBackgroundColor(Color.parseColor("#87CEEB"));
            }
        	break;
        case 2:
        	if(position%2==0){
                holder.labelText.setBackgroundColor(Color.parseColor("#FF8800"));
            }else{
                holder.labelText.setBackgroundColor(Color.parseColor("#FFBB33"));
            }
        	break;
        case 3:
        	if(position%2==0){
                holder.labelText.setBackgroundColor(Color.parseColor("#FF00FF"));
            }else{
                holder.labelText.setBackgroundColor(Color.parseColor("#FFB6C1"));
            }
        	break;
        case 4:
        	if(position%2==0){
                holder.labelText.setBackgroundColor(Color.parseColor("#CC0000"));
            }else{
                holder.labelText.setBackgroundColor(Color.parseColor("#FA8072"));
            }
        	break;
        case 5:
        	if(position%2==0){
                holder.labelText.setBackgroundColor(Color.parseColor("#99CC00"));
            }else{
                holder.labelText.setBackgroundColor(Color.parseColor("#32CD32"));
            }
        	break;
        }
        
        // �s���ɔw�i�F��ς���
        

        // XML�Œ�`�����A�j���[�V������ǂݍ���
        Animation anim = AnimationUtils.loadAnimation(getContext(), R.anim.list_motion);
        // ���X�g�A�C�e���̃A�j���[�V�������J�n
        view.startAnimation(anim);

        return view;
    }
}
