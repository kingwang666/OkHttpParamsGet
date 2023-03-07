package com.wang.okhttpparamsget;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Settings implements Configurable, ActionListener {

    private JPanel panel;
    private JRadioButton empty_string_rb;
    private JRadioButton not_put_rb;
    private JRadioButton support_rb;
    private JRadioButton androidx_rb;
    private JRadioButton nullable_rb;
    private JRadioButton nonnull_rb;
    private JRadioButton hash_map_rb;
    private JRadioButton array_map_rb;
    private JCheckBox okhttp_cb;

    private boolean add;
    private boolean arrayMap;
    private boolean androidx;
    private boolean nullable;
    private boolean version4;


    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return "OkHttpParamsGet";
    }


    @Nullable
    @Override
    public JComponent createComponent() {
        initSettings();
        not_put_rb.addActionListener(this);
        empty_string_rb.addActionListener(this);
        hash_map_rb.addActionListener(this);
        array_map_rb.addActionListener(this);
        support_rb.addActionListener(this);
        androidx_rb.addActionListener(this);
        nullable_rb.addActionListener(this);
        nonnull_rb.addActionListener(this);
        return panel;
    }

    @Override
    public boolean isModified() {
        return isModified(empty_string_rb, add) || isModified(array_map_rb, arrayMap) || isModified(androidx_rb, androidx)
                || isModified(nullable_rb, nullable) || isModified(okhttp_cb, version4);
    }

    private void initSettings() {
        add = PropertiesComponent.getInstance().getBoolean(Constant.VALUE_NULL, false);
        arrayMap = PropertiesComponent.getInstance().getBoolean(Constant.ARRAY_MAP, true);
        androidx = PropertiesComponent.getInstance().getBoolean(Constant.ANDROIDX, true);
        nullable = PropertiesComponent.getInstance().getBoolean(Constant.NULLABLE, true);
        version4 = PropertiesComponent.getInstance().getBoolean(Constant.OKHTTP_VERSION, true);
        reset();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(not_put_rb)) {
            if (not_put_rb.isSelected()) {
                empty_string_rb.setSelected(false);
            } else {
                not_put_rb.setSelected(true);
            }
        } else if (e.getSource().equals(empty_string_rb)) {
            if (empty_string_rb.isSelected()) {
                not_put_rb.setSelected(false);
            } else {
                empty_string_rb.setSelected(true);
            }
        } else if (e.getSource().equals(hash_map_rb)) {
            if (hash_map_rb.isSelected()) {
                array_map_rb.setSelected(false);
            } else {
                hash_map_rb.setSelected(true);
            }
        } else if (e.getSource().equals(array_map_rb)) {
            if (array_map_rb.isSelected()) {
                hash_map_rb.setSelected(false);
            } else {
                array_map_rb.setSelected(true);
            }
        } else if (e.getSource().equals(support_rb)) {
            if (support_rb.isSelected()) {
                androidx_rb.setSelected(false);
            } else {
                support_rb.setSelected(true);
            }
        } else if (e.getSource().equals(androidx_rb)) {
            if (androidx_rb.isSelected()) {
                support_rb.setSelected(false);
            } else {
                androidx_rb.setSelected(true);
            }
        } else if (e.getSource().equals(nullable_rb)) {
            if (nullable_rb.isSelected()) {
                nonnull_rb.setSelected(false);
            } else {
                nullable_rb.setSelected(true);
            }
        } else if (e.getSource().equals(nonnull_rb)) {
            if (nonnull_rb.isSelected()) {
                nullable_rb.setSelected(false);
            } else {
                nonnull_rb.setSelected(true);
            }
        }
    }


    @Override
    public void apply() throws ConfigurationException {
        PropertiesComponent.getInstance().setValue(Constant.VALUE_NULL, empty_string_rb.isSelected());
        PropertiesComponent.getInstance().setValue(Constant.ARRAY_MAP, array_map_rb.isSelected(), true);
        PropertiesComponent.getInstance().setValue(Constant.ANDROIDX, androidx_rb.isSelected(), true);
        PropertiesComponent.getInstance().setValue(Constant.NULLABLE, nullable_rb.isSelected(), true);
        PropertiesComponent.getInstance().setValue(Constant.OKHTTP_VERSION, okhttp_cb.isSelected(), true);
    }

    @Override
    public void reset() {

        empty_string_rb.setSelected(add);
        not_put_rb.setSelected(!add);

        array_map_rb.setSelected(arrayMap);
        hash_map_rb.setSelected(!arrayMap);

        androidx_rb.setSelected(androidx);
        support_rb.setSelected(!androidx);

        nullable_rb.setSelected(nullable);
        nonnull_rb.setSelected(!nullable);

        okhttp_cb.setSelected(version4);
    }

    @Override
    public void disposeUIResources() {
        not_put_rb.removeActionListener(this);
        empty_string_rb.removeActionListener(this);
        hash_map_rb.removeActionListener(this);
        array_map_rb.removeActionListener(this);
        support_rb.removeActionListener(this);
        androidx_rb.removeActionListener(this);
        nullable_rb.removeActionListener(this);
        nonnull_rb.removeActionListener(this);
    }

}
