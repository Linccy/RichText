package com.linccy.richtext;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.linccy.richtext.entity.InsertEntity;

/**
 * 简单的自定义@和话题功能的AppCompatEditText
 *
 * @author lin.cx 957109587@qq.com
 * @version 3.0
 */
public class SimpleRichEditor extends EditText {
    private int maxLength = 2000;
    private List<InsertEntity> insertModelList = new ArrayList<>();

    private static final int BACKGROUND_COLOR = Color.parseColor("#FFDEAD"); // 默认,话题背景高亮颜色
    private Context mContext;
    private OnEditListener editListener;
    private boolean canInsertSame = true; //是否能插入相同的内容
    private boolean isRequestDisallowInterceptTouchEvent = false;//是否阻止父层的View截获touch事件
    public static final String AT = "@";
    public static final String TAG = "฿";
    private static final String[] REGEXS = {"@[^@\\s]{1,20}", "฿[^@#฿]+?฿"};

    public SimpleRichEditor(Context context) {
        this(context, null);
    }

    public SimpleRichEditor(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        if (isInEditMode())
            return;
        InputFilter[] filters = {new InputFilter.LengthFilter(maxLength)};
        setFilters(filters);
        initView();
    }

    public SimpleRichEditor(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    @SuppressWarnings("NewApi")
    public SimpleRichEditor(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.mContext = context;
        if (isInEditMode())
            return;
        InputFilter[] filters = {new InputFilter.LengthFilter(maxLength)};
        setFilters(filters);
        initView();
    }

    /**
     * 初始化控件,监听输入@和฿
     */
    private void initView() {

        this.addTextChangedListener(new TextWatcher() {
            private String inputKeyWord = null;
            private boolean isFirstInput = false;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int index = SimpleRichEditor.this.getSelectionStart();
                if (index > 0) {
                    if (s == null) {
                        return;
                    }
                    String input = s.toString().substring(index - 1, index);
                    if (count == 1 && editListener != null) {
                        inputKeyWord = input;
                        isFirstInput = true;
                    } else {
                        inputKeyWord = null;
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (AT.equals(inputKeyWord) && isFirstInput) {
                    editListener.onInputAt();
                    isFirstInput = false;
                } else if (TAG.equals(inputKeyWord) && isFirstInput) {
                    editListener.onInputTag();
                    isFirstInput = false;
                }
                resolveDeleteSpecialStr();
            }
        });

        /**
         * 监听删除键
         * 1.光标在话题后面,将整个话题内容删除
         * 2.光标在普通文字后面,删除一个字符
         *
         */
        this.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN) {

                    int selectionStart = getSelectionStart();
                    int selectionEnd = getSelectionEnd();

                    /**
                     * 如果光标起始和结束不在同一位置,删除文本
                     */
                    if (selectionStart != selectionEnd) {
                        // 查询文本是否属于目标对象,若是移除列表数据
                        String targetText = getText().toString().substring(
                                selectionStart, selectionEnd);
                        for (int i = 0; i < insertModelList.size(); i++) {
                            InsertEntity object = insertModelList.get(i);
                            if (targetText.equals(object.getInsertContent())) {
                                insertModelList.remove(object);
                                if (editListener != null) {
                                    editListener.onDeleteInsetModel(object);
                                }
                                break;
                            }
                        }
                        return false;
                    }


                    int lastPos = 0;
                    Editable editable = getText();
                    // 遍历判断光标的位置
                    for (int i = 0; i < insertModelList.size(); i++) {
                        String objectText = insertModelList.get(i).getInsertContent();
                        lastPos = getText().toString().indexOf(objectText, lastPos);
                        if (lastPos != -1) {
                            if (selectionStart != 0 && selectionStart >= lastPos && selectionStart <= (lastPos + objectText.length())) {
                                // 选中话题
                                setSelection(lastPos, lastPos + objectText.length());
                                // 设置背景色
                                editable.setSpan(new BackgroundColorSpan(BACKGROUND_COLOR), lastPos, lastPos + objectText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                return true;
                            }
                        }
                        lastPos += objectText.length();
                    }
                }

                return false;
            }
        });
    }

    /**
     * 监听光标的位置,若光标处于话题内容中间则移动光标到话题结束位置
     */
    @Override
    protected void onSelectionChanged(int selStart, int selEnd) {
        super.onSelectionChanged(selStart, selEnd);
        if (insertModelList == null || insertModelList.size() == 0)
            return;
        int startPosition;
        int endPosition;
        String insertContent;
        for (int i = 0; i < insertModelList.size(); i++) {
            insertContent = insertModelList.get(i).getInsertContent();
            startPosition = getText().toString().indexOf(insertContent);
            endPosition = startPosition + insertContent.length();
            if (startPosition != -1 && selStart > startPosition
                    && selStart <= endPosition) {// 若光标处于话题内容中间则移动光标到话题结束位置
                setSelection(endPosition);
            }
        }
    }

    /**
     * @param insertModel 插入对象
     */
    public void insertSpecialStr(InsertEntity insertModel) {
        if (insertModel == null)
            return;

        if (!canInsertSame) {
            for (InsertEntity model : insertModelList) {
                if ((model.getInsertContent().replace(model.getInsertRule(), "")).equals(insertModel.getInsertContent()) && model.getInsertRule().equals(insertModel.getInsertRule())) {
                    Toast.makeText(mContext, "不可重复插入", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        }
        String insertRule = insertModel.getInsertRule();
        String insertContent = insertModel.getInsertContent();
        String insertColor = insertModel.getInsertColor();
        if (TextUtils.isEmpty(insertRule) || TextUtils.isEmpty(insertContent)) {
            return;
        }
        if (insertRule.equals(AT)) {
            insertContent = insertRule + insertContent + " ";
        } else if (insertRule.equals(TAG)) {
            insertContent = insertRule + insertContent + insertRule + " ";
        }
        insertModel.setInsertContent(insertContent);

        insertModelList.add(insertModel);

        //将特殊字符插入到EditText 中显示
        int index = getSelectionStart();//光标位置
        Editable editable = getText();//原先内容
//    if (String.valueOf(editable.toString().substring(index - insertRule.length(), index)).equals(insertRule)) {
//      editable.replace(index - insertRule.length(), index, " ");
//      index = index - insertRule.length();
//    }
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(editable);
        spannableStringBuilder.insert(index, insertContent);
        spannableStringBuilder.setSpan(new ForegroundColorSpan(Color.parseColor(insertColor)), index, index + insertContent.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        setText(spannableStringBuilder, BufferType.SPANNABLE);
        setSelection(index + insertContent.length());
    }

    /**
     * 转换为富文本
     */
    public void setRichText(@Nullable SpannableStringBuilder content) {
        setText(content);
        if (TextUtils.isEmpty(content)) {
            return;
        }
        for (String regex : REGEXS) {
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(content);
            while (matcher.find()) {
                InsertEntity entity = new InsertEntity("", content.subSequence(matcher.start(), matcher.end()).toString());
                insertModelList.add(entity);
            }
        }
    }

    /**
     * 获取普通文本内容
     */
    public String getRichContent() {
        String content = getText().toString();
        if (insertModelList != null && insertModelList.size() > 0) {
            for (int i = 0; i < insertModelList.size(); i++) {
                InsertEntity inertModel = insertModelList.get(i);
                content = content.replace(inertModel.getInsertContent(), "");
            }
        }
        return content.trim();
    }

    /**
     * 获取特殊字符列表
     */
    public List<InsertEntity> getRichInsertList() {
        List<InsertEntity> objectsList = new ArrayList<>();
        if (insertModelList != null && insertModelList.size() > 0) {
            for (int i = 0; i < insertModelList.size(); i++) {
                InsertEntity inertModel = insertModelList.get(i);
                objectsList.add(new InsertEntity(inertModel.getInsertRule(), inertModel.getInsertContent().replace(inertModel.getInsertRule(), ""), inertModel.getInsertColor()));
            }
        }
        return objectsList;
    }


    /**
     * 删除缓存列表
     */
    private void resolveDeleteSpecialStr() {
        String tagetText = getText().toString();
        if (TextUtils.isEmpty(tagetText)) {
            insertModelList.clear();
            return;
        }
        for (int i = 0; i < insertModelList.size(); i++) {
            InsertEntity object = insertModelList.get(i);
            if (!tagetText.contains(object.getInsertContent())) {
                insertModelList.remove(object);
            }
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        getParent().requestDisallowInterceptTouchEvent(isRequestDisallowInterceptTouchEvent);
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_UP:
                getParent().requestDisallowInterceptTouchEvent(false);
                break;
            case MotionEvent.ACTION_DOWN:
                this.requestFocus();
                break;
            default:
                break;
        }
        return super.onTouchEvent(event);
    }

    public boolean isRequestDisallowInterceptTouchEvent() {
        return isRequestDisallowInterceptTouchEvent;
    }

    //是否可以点击滑动
    public void setRequestDisallowInterceptTouchEvent(boolean requestDisallowInterceptTouchEvent) {
        isRequestDisallowInterceptTouchEvent = requestDisallowInterceptTouchEvent;
    }

    public int getEditTextMaxLength() {
        return maxLength;
    }


    //最大可输入长度
    public void setEditTextMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }

    public void setOnEditListener(OnEditListener editListener) {
        this.editListener = editListener;
    }

    public void setCanInsertSame(boolean canInsertSame) {
        this.canInsertSame = canInsertSame;
    }

    public interface OnEditListener {
        /**
         * 当输入@时
         */
        void onInputAt();

        /**
         * 当输入#时
         */
        void onInputTag();

        void onDeleteInsetModel(InsertEntity model);
    }

}
