package me.riddhimanadib.formmaster.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import me.riddhimanadib.formmaster.R;
import me.riddhimanadib.formmaster.listener.FormItemEditTextListener;
import me.riddhimanadib.formmaster.listener.OnFormElementValueChangedListener;
import me.riddhimanadib.formmaster.listener.ReloadListener;
import me.riddhimanadib.formmaster.model.BaseFormElement;
import me.riddhimanadib.formmaster.model.FormElementFile;
import me.riddhimanadib.formmaster.model.FormElementPickerSingle;
import me.riddhimanadib.formmaster.viewholder.BaseViewHolder;
import me.riddhimanadib.formmaster.viewholder.FormElementButtonViewHolder;
import me.riddhimanadib.formmaster.viewholder.FormElementCheckBoxViewHolder;
import me.riddhimanadib.formmaster.viewholder.FormElementFileViewHolder;
import me.riddhimanadib.formmaster.viewholder.FormElementGroupQuestionViewHolder;
import me.riddhimanadib.formmaster.viewholder.FormElementHeader;
import me.riddhimanadib.formmaster.viewholder.FormElementImageViewHolder;
import me.riddhimanadib.formmaster.viewholder.FormElementLocationSuggestViewHolder;
import me.riddhimanadib.formmaster.viewholder.FormElementMusicFileViewHolder;
import me.riddhimanadib.formmaster.viewholder.FormElementPickerDateViewHolder;
import me.riddhimanadib.formmaster.viewholder.FormElementPickerMultiViewHolder;
import me.riddhimanadib.formmaster.viewholder.FormElementPickerSingleViewHolder;
import me.riddhimanadib.formmaster.viewholder.FormElementPickerTimeViewHolder;
import me.riddhimanadib.formmaster.viewholder.FormElementRatingViewHolder;
import me.riddhimanadib.formmaster.viewholder.FormElementSwitchViewHolder;
import me.riddhimanadib.formmaster.viewholder.FormElementTextEmailViewHolder;
import me.riddhimanadib.formmaster.viewholder.FormElementTextMultiLineViewHolder;
import me.riddhimanadib.formmaster.viewholder.FormElementTextNumberViewHolder;
import me.riddhimanadib.formmaster.viewholder.FormElementTextPasswordViewHolder;
import me.riddhimanadib.formmaster.viewholder.FormElementTextPhoneViewHolder;
import me.riddhimanadib.formmaster.viewholder.FormElementTextSingleLineViewHolder;
import me.riddhimanadib.formmaster.viewholder.FormElementTextViewHolder;
import me.riddhimanadib.formmaster.viewholder.FormElementTitleHolder;
import me.riddhimanadib.formmaster.viewholder.FormElementUrlViewHolder;

/**
 * The adapter the holds and displays the form objects
 * Created by Adib on 16-Apr-17.
 */

public class FormAdapter extends RecyclerView.Adapter<BaseViewHolder> implements ReloadListener {

    private Context mContext;
    private List<BaseFormElement> mDataset;
    private OnFormElementValueChangedListener mListener;

    /**
     * public constructor with context
     *
     * @param context
     */
    public FormAdapter(Context context, OnFormElementValueChangedListener listener) {
        mContext = context;
        mListener = listener;
        mDataset = new ArrayList<>();
    }

    /**
     * adds list of elements to be shown
     *
     * @param formObjects
     */
    public void addElements(List<BaseFormElement> formObjects) {
        this.mDataset = formObjects;
        notifyDataSetChanged();
    }

    /**
     * adds single element to be shown
     *
     * @param formObject
     */
    public void addElement(BaseFormElement formObject) {
        this.mDataset.add(formObject);
        notifyDataSetChanged();
    }

    /**
     * set value for any unique index
     *
     * @param position
     * @param value
     */
    public void setValueAtIndex(int position, String value) {
        mDataset.get(position).setValue(value);
        notifyDataSetChanged();
    }

    /**
     * set value for any unique tag
     *
     * @param tag
     * @param value
     */
    public void setValueAtTag(int tag, String value) {
        for (BaseFormElement f : this.mDataset) {
            if (f.getTag() == tag) {
                f.setValue(value);
            }
        }
        notifyDataSetChanged();
    }

    public void setValueByName(String name, String value) {
        List<BaseFormElement> mDataset1 = this.mDataset;
        for (int i = 0; i < mDataset1.size(); i++) {
            BaseFormElement f = mDataset1.get(i);
            if (name.equals(f.getName())) {
                f.setValue(value);
                notifyItemChanged(i);
                break;
            }
        }
        // notifyDataSetChanged();
    }

    public void setValueAtTagNonRefresh(int tag, String value) {
        for (BaseFormElement f : this.mDataset) {
            if (f.getTag() == tag) {
                f.setValue(value);
                break;
            }
        }
    }

   /* public void setValueAtTag(int tag1, int tag2, String value) {
        for (BaseFormElement f : this.mDataset) {
            if (f.getTag() == tag) {
                f.setValue(value);
                break;
            }
        }
        notifyDataSetChanged();
    }*/

    public void setThumbnailAtTag(int tag, String value) {
        for (BaseFormElement f : this.mDataset) {
            if (f.getTag() == tag) {
                ((FormElementFile) f).setThumbnail(value);
                break;
            }
        }
        notifyDataSetChanged();
    }

    public void setHiddenAtTag(int tag, boolean isHidden) {
        for (BaseFormElement f : this.mDataset) {
            if (f.getTag() == tag) {
                Log.e("Tag1:"+tag,"TAG2:"+f.getTag());
                Log.e("Hiden: ",""+isHidden);
                f.setHidden(isHidden);
                break;
            }
        }
    }




    public void setHiddenAtType(int tag, boolean isHidden) {
        for (BaseFormElement f : this.mDataset) {
            if (f.getTag() == tag) {
                 f.setHidden(isHidden);
                break;
            }
        }
    }

    public void setHiddenAtType12(int tag, boolean isHidden) {
        for (BaseFormElement f : this.mDataset) {
            if (f.getTag() == tag) {
                f.setRequired(false);
                break;
            }
        }
    }


   /* public void setHiddenAtTagMuliple(int tag, boolean isHidden) {
        for (BaseFormElement f : this.mDataset) {
            if (f.getTag() == tag) {
                f.setHidden(isHidden);
                break;
            }
        }
    }
*/

    /**
     * set value for any unique tag
     *
     * @param tag
     * @param options
     */
    public void setOptionAtTag(int tag, List<String> options) {
        try {
            for (BaseFormElement f : this.mDataset) {
                if (f.getTag() == tag) {
                    ((FormElementPickerSingle) f).setOptions(options);
                    ((FormElementPickerSingle) f).setValue("");
                    Log.e("setOptionAtTag", "setOptionAtTag");
                    break;
                }
            }
            notifyDataSetChanged();
        } catch (Exception e) {
            Log.e("setOptionAtTag", e.getMessage());
        }
    }



    /**
     * get value of any element by tag
     *
     * @param index
     * @return
     */
    public BaseFormElement getValueAtIndex(int index) {
        return (mDataset.get(index));
    }

    /**
     * get value of any element by tag
     *
     * @param tag
     * @return
     */
    public BaseFormElement getValueAtTag(int tag) {
        for (BaseFormElement f : this.mDataset) {
            if (f.getTag() == tag) {
                return f;
            }
        }

        return null;
    }

    public String getValueByName(String name) {
        for (BaseFormElement f : this.mDataset) {
            Log.d("name", name + "=" + f.getName());
            if (name.equals(f.getName())) {
                return f.getValue();
            }
        }
        return null;
    }

    /**
     * get whole dataset
     *
     * @return
     */
    public List<BaseFormElement> getDataset() {
        return mDataset;
    }

    /**
     * get value changed listener
     *
     * @return
     */
    public OnFormElementValueChangedListener getValueChangeListener() {
        return mListener;
    }

    /**
     * gets total item count
     *
     * @return
     */
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    /**
     * gets view item type based on header, or the form element type
     *
     * @param position
     * @return
     */
    @Override
    public int getItemViewType(int position) {
        return mDataset.get(position).getType();
    }

    /**
     * creating the view holder to be shown for a position
     *
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        // get layout based on header or element type
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v;
        switch (viewType) {
            case BaseFormElement.TYPE_HEADER:
                v = inflater.inflate(R.layout.form_element_header, parent, false);
                return new FormElementHeader(v);
            case BaseFormElement.TYPE_EDITTEXT_TEXT_SINGLELINE:
                v = inflater.inflate(R.layout.form_element, parent, false);
                return new FormElementTextSingleLineViewHolder(v, new FormItemEditTextListener(this));
            case BaseFormElement.TYPE_LOCATION_SUGGEST:
                v = inflater.inflate(R.layout.form_element_location, parent, false);
                return new FormElementLocationSuggestViewHolder(v, new FormItemEditTextListener(this));
            case BaseFormElement.TYPE_EDITTEXT_TEXT_MULTILINE:
                v = inflater.inflate(R.layout.form_element_textarea, parent, false);
                return new FormElementTextMultiLineViewHolder(v, new FormItemEditTextListener(this));
            case BaseFormElement.TYPE_EDITTEXT_NUMBER:
                v = inflater.inflate(R.layout.form_element, parent, false);
                return new FormElementTextNumberViewHolder(v, new FormItemEditTextListener(this));
            case BaseFormElement.TYPE_EDITTEXT_EMAIL:
                v = inflater.inflate(R.layout.form_element, parent, false);
                return new FormElementTextEmailViewHolder(v, new FormItemEditTextListener(this));
            case BaseFormElement.TYPE_EDITTEXT_PHONE:
                v = inflater.inflate(R.layout.form_element, parent, false);
                return new FormElementTextPhoneViewHolder(v, new FormItemEditTextListener(this));
            case BaseFormElement.TYPE_EDITTEXT_PASSWORD:
                v = inflater.inflate(R.layout.form_element, parent, false);
                return new FormElementTextPasswordViewHolder(v, new FormItemEditTextListener(this));
            case BaseFormElement.TYPE_PICKER_DATE:
                v = inflater.inflate(R.layout.form_element_select, parent, false);
                return new FormElementPickerDateViewHolder(v, mContext, this);
            case BaseFormElement.TYPE_PICKER_TIME:
                v = inflater.inflate(R.layout.form_element_select, parent, false);
                return new FormElementPickerTimeViewHolder(v, mContext, this);
            case BaseFormElement.TYPE_PICKER_SINGLE:
                v = inflater.inflate(R.layout.form_element_select, parent, false);
                return new FormElementPickerSingleViewHolder(v, mContext, this);
            case BaseFormElement.TYPE_PICKER_MULTI:
                v = inflater.inflate(R.layout.form_element_select, parent, false);
                return new FormElementPickerMultiViewHolder(v, mContext, this);
            case BaseFormElement.TYPE_SWITCH:
                v = inflater.inflate(R.layout.form_element_switch, parent, false);
                return new FormElementSwitchViewHolder(v, mContext, this);
            case BaseFormElement.TYPE_RATING:
                v = inflater.inflate(R.layout.form_element_rating, parent, false);
                return new FormElementRatingViewHolder(v, mContext, this);
            case BaseFormElement.TYPE_BUTTON:
                v = inflater.inflate(R.layout.form_element_button, parent, false);
                return new FormElementButtonViewHolder(v);
            case BaseFormElement.TYPE_CHECKBOX:
                v = inflater.inflate(R.layout.form_element_checkbox, parent, false);
                return new FormElementCheckBoxViewHolder(v, mContext, this);
            case BaseFormElement.TYPE_FILE:
                v = inflater.inflate(R.layout.form_element_file, parent, false);
                return new FormElementFileViewHolder(v, mContext, this);
            case BaseFormElement.TYPE_IMAGE_TYPE:
                v = inflater.inflate(R.layout.form_element_image, parent, false);
                return new FormElementImageViewHolder(v, mContext, this);
            case BaseFormElement.TYPE_GROUP_QUESTION:
                v = inflater.inflate(R.layout.form_element_group_question, parent, false);
                return new FormElementGroupQuestionViewHolder(v, mContext, this);
            case BaseFormElement.TYPE_MUSIC_FILE:
                v = inflater.inflate(R.layout.form_element_music_file, parent, false);
                return new FormElementMusicFileViewHolder(v, mContext, this);
            case BaseFormElement.TYPE_TITLE:
                v = inflater.inflate(R.layout.form_element_title, parent, false);
                return new FormElementTitleHolder(v);
            case BaseFormElement.TYPE_EDITOR:
                v = inflater.inflate(R.layout.form_element_textview, parent, false);
                return new FormElementTextViewHolder(v);
            case BaseFormElement.TYPE_UNEDITABLE_TEXT:
                v = inflater.inflate(R.layout.form_element_textview, parent, false);
                return new FormElementTextViewHolder(v);
            case BaseFormElement.TYPE_UNEDITABLE_TEXT_CENTER:
                v = inflater.inflate(R.layout.layout_fixed_text_center, parent, false);
                return new FormElementTextViewHolder(v);
			case BaseFormElement.TYPE_URL:
                v = inflater.inflate(R.layout.form_element_url, parent, false);
                return new FormElementUrlViewHolder(v ,new FormItemEditTextListener(this));
            default:
                v = inflater.inflate(R.layout.form_element, parent, false);
                return new FormElementTextSingleLineViewHolder(v, new FormItemEditTextListener(this));
        }
    }

    /**
     * draws the view for the position specific view holder
     *
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(BaseViewHolder holder, final int position) {

        // updates edit text listener index
        if (holder.getListener() != null) {
            holder.getListener().updatePosition(holder.getAdapterPosition());
        }

        // gets current object
        BaseFormElement currentObject = mDataset.get(position);
        holder.bind(position, currentObject, mContext);
    }

    /**
     * use the listener to update value and notify dataset changes to adapter
     *
     * @param position
     * @param updatedValue
     */
    @Override
    public void updateValue(int position, String updatedValue) {
        mDataset.get(position).setValue(updatedValue);
        notifyDataSetChanged();
        if (mListener != null)
            mListener.onValueChanged(mDataset.get(position));
    }

}