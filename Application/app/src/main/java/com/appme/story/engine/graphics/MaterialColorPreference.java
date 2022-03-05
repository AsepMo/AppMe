package com.appme.story.engine.graphics;

import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceViewHolder;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.InsetDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.appme.story.R;
import com.appme.story.engine.app.utils.ScreenUtils;
import com.appme.story.engine.graphics.ColorPalette;
import com.appme.story.application.ApplicationPreferences;
import com.appme.story.settings.theme.ThemePreference;
/**
 * A preference that allows the user to choose an application or shortcut.
 */
public class MaterialColorPreference extends Preference {
    public static final int TYPE_PRIMARY = 0;
    public static final int TYPE_ACCENT = 1;
    private AppCompatActivity mActivity;
    private Context mContext;
    private int[] mColorChoices = {};
    private int mValue = 0;
    private int mItemLayoutId = R.layout.pref_layout_color;
    private int colorType;
    private View mPreviewView;
    private static OnColorPickerListener mOnColorPickerListener;
    
    public MaterialColorPreference(Context context) {
        super(context);
        initAttrs(context, null, 0);
    }

    public MaterialColorPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(context, attrs, 0);
    }

    public MaterialColorPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initAttrs(context, attrs, defStyle);
    }

    private void initAttrs(Context context, AttributeSet attrs, int defStyle) {
        mContext = context;
        TypedArray a = getContext().getTheme().obtainStyledAttributes(
			attrs, R.styleable.ColorPreference, defStyle, defStyle);

        try {
            mItemLayoutId = a.getResourceId(R.styleable.ColorPreference_itemLayout, mItemLayoutId);
            colorType = a.getInt(R.styleable.ColorPreference_color_type, TYPE_PRIMARY);
            int choicesResId = a.getResourceId(R.styleable.ColorPreference_choices, R.array.default_color_choice_values);
            if (choicesResId > 0) {
                String[] choices = a.getResources().getStringArray(choicesResId);
                mColorChoices = new int[choices.length];
                for (int i = 0; i < choices.length; i++) {
                    mColorChoices[i] = Color.parseColor(choices[i]);
                }
            }

        } finally {
            a.recycle();
        }

        setWidgetLayoutResource(mItemLayoutId);
    }

    @Override
    public void onBindViewHolder(PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);
        mPreviewView = holder.findViewById(R.id.color_view);
        setColorViewValue(mPreviewView, mValue, false);
        
        ColorDialogFragment fragment = (ColorDialogFragment) mActivity.getSupportFragmentManager().findFragmentByTag(getFragmentTag());
        if (fragment != null) {
            // re-bind preference to fragment
            fragment.setPreference(this);
            fragment.setColorType(colorType);
        }
    }

    public void onAttact(AppCompatActivity activity){
        this.mActivity = activity;
    }
    
    public void setValue(int value) {
        if (callChangeListener(value)) {
            mValue = value;
            persistInt(value);
            notifyChanged();
        }
    }

    @Override
    protected void onClick() {
        super.onClick();

        ColorDialogFragment fragment = ColorDialogFragment.newInstance();
        fragment.setPreference(this);
        fragment.setColorType(colorType);

        mActivity.getSupportFragmentManager().beginTransaction()
			.add(fragment, getFragmentTag())
			.commit();
    }

    @Override
    public void onAttached() {
        super.onAttached();
        
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getInt(index, 0);
    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
        setValue(restoreValue ? getPersistedInt(0) : (Integer) defaultValue);
    }

    public String getFragmentTag() {
        return "color_" + getKey();
    }

    public int getValue() {
        return mValue;
    }

    public static class ColorDialogFragment extends AppCompatDialogFragment {
        private MaterialColorPreference mPreference;
        private LineColorPicker mColorPicker;
        private LineColorPicker mShadePicker;
        private TextView mTitle;
        int colorType;

        public ColorDialogFragment() {
        }

        public static ColorDialogFragment newInstance() {
            return new ColorDialogFragment();
        }

        public void setPreference(MaterialColorPreference preference) {
            mPreference = preference;
        }

        public void setColorType(int type){
            colorType = type;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
        }

        @Override
        public AppCompatDialog onCreateDialog(Bundle savedInstanceState) {
            final Context context = getActivity();

            final AlertDialog.Builder builder = new AlertDialog.Builder(context);
            final LayoutInflater dialogInflater = LayoutInflater.from(builder.getContext());

            final View rootView = dialogInflater.inflate(R.layout.layout_color_preference, null, false);

            mColorPicker = (LineColorPicker) rootView.findViewById(R.id.color_picker);
            mShadePicker = (LineColorPicker) rootView.findViewById(R.id.shade_picker);
            mTitle = (TextView) rootView.findViewById(R.id.title);

            int color = ThemePreference.getPrimaryColor();
            if(colorType == 0){
                mTitle.setText(R.string.title_primary_color);
                mColorPicker.setColors(ColorPalette.getBaseColors(context));
                for (int i : mColorPicker.getColors()) {
                    for (int j : ColorPalette.getColors(context, i)) {
                        if (j == color) {
                            mColorPicker.setSelectedColor(i);
                            mShadePicker.setColors(ColorPalette.getColors(context, i));
                            mShadePicker.setSelectedColor(j);
                            break;
                        }
                    }
                }
            }
            else if(colorType == 1){
                mTitle.setText(R.string.title_accent_color);
                mColorPicker.setColors(ColorPalette.getAccentColors(context));
                mShadePicker.setVisibility(View.GONE);
                color = ThemePreference.getAccentColor();
                mColorPicker.setSelectedColor(color);
            }

            mTitle.setBackgroundColor(color);
            mColorPicker.setOnColorChangedListener(new LineColorPicker.OnColorChangedListener() {
					@Override
					public void onColorChanged(int c) {
						mTitle.setBackgroundColor(c);
						if(colorType == 0) {
							mShadePicker.setColors(ColorPalette.getColors(context, mColorPicker.getColor()));
							mShadePicker.setSelectedColor(mColorPicker.getColor());
                            if(mOnColorPickerListener != null){
                                mOnColorPickerListener.onColorPicker(mColorPicker.getColor());
                            }
                             ((ApplicationPreferences)getActivity()).changeActionBarColor(mColorPicker.getColor());
						}
					}
				});
            mShadePicker.setOnColorChangedListener(new LineColorPicker.OnColorChangedListener() {
					@Override
					public void onColorChanged(int c) {
						mTitle.setBackgroundColor(c);
					}
				});

            builder.setView(rootView);

            builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if(colorType == 0) {
							mPreference.setValue(mShadePicker.getColor());
                            ThemePreference.setAccentColor(ColorPalette.getAccentColor(context, mColorPicker.getColor()));                              
						} else if (colorType == 1) {
							mPreference.setValue(mColorPicker.getColor());                                     
						}
						dismiss();
                        ((ApplicationPreferences)getActivity()).recreate();
					}
				});
            builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
						if(colorType == 0) {
                             if(mOnColorPickerListener != null){
                                mOnColorPickerListener.onColorPicker(ThemePreference.getPrimaryColor());
                            }
                            ThemePreference.setPrimaryColor(ThemePreference.getPrimaryColor());                                         
                            ((ApplicationPreferences)getActivity()).changeActionBarColor(ThemePreference.getPrimaryColor());
						}
						dismiss();
					}
				});
            return builder.create();
        }
    }

    private static void setColorViewValue(View view, int color, boolean selected) {
        if (view instanceof ImageView) {
            ImageView imageView = (ImageView) view;
            Resources res = imageView.getContext().getResources();

            Drawable currentDrawable = imageView.getDrawable();
            GradientDrawable colorChoiceDrawable;
            if (currentDrawable instanceof GradientDrawable) {
                // Reuse drawable
                colorChoiceDrawable = (GradientDrawable) currentDrawable;
            } else {
                colorChoiceDrawable = new GradientDrawable();
                colorChoiceDrawable.setShape(GradientDrawable.OVAL);
            }

            // Set stroke to dark version of color
            int darkenedColor = Color.rgb(
				Color.red(color) * 192 / 256,
				Color.green(color) * 192 / 256,
				Color.blue(color) * 192 / 256);

            colorChoiceDrawable.setColor(color);
            colorChoiceDrawable.setStroke((int) TypedValue.applyDimension(
											  TypedValue.COMPLEX_UNIT_DIP, 2, res.getDisplayMetrics()), darkenedColor);

            Drawable drawable = colorChoiceDrawable;
            if (selected) {

                VectorDrawableCompat checkmark = VectorDrawableCompat.create(view.getResources(), R.drawable.checkmark_white, null);
                InsetDrawable checkmarkInset = new InsetDrawable(checkmark, ScreenUtils.dpToPx(5));
                drawable = new LayerDrawable(new Drawable[]{
												 colorChoiceDrawable,
												 checkmarkInset});
            }

            imageView.setImageDrawable(drawable);

        } else if (view instanceof TextView) {
            ((TextView) view).setTextColor(color);
        }
    }
}
