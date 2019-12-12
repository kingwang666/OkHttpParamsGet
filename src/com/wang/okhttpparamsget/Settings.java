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


    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return "OkHttpParamsGet";
    }

    @Nullable
    @Override
    public String getHelpTopic() {
        return null;
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        reset();
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
    public boolean isModified() {
        return true;
    }

    @Override
    public void apply() throws ConfigurationException {
        PropertiesComponent.getInstance().setValue(Constant.VALUE_NULL, empty_string_rb.isSelected());
        PropertiesComponent.getInstance().setValue(Constant.ARRAY_MAP, array_map_rb.isSelected(), true);
        PropertiesComponent.getInstance().setValue(Constant.ANDROIDX, androidx_rb.isSelected(), true);
        PropertiesComponent.getInstance().setValue(Constant.NULLABLE, nullable_rb.isSelected(), true);
    }

    @Override
    public void reset() {
        boolean add = PropertiesComponent.getInstance().getBoolean(Constant.VALUE_NULL, false);
        boolean arrayMap = PropertiesComponent.getInstance().getBoolean(Constant.ARRAY_MAP, true);
        boolean androidx = PropertiesComponent.getInstance().getBoolean(Constant.ANDROIDX, true);
        boolean nullable = PropertiesComponent.getInstance().getBoolean(Constant.NULLABLE, true);
        if (add) {
            empty_string_rb.setSelected(true);
        } else {
            not_put_rb.setSelected(true);
        }
        if (arrayMap){
            array_map_rb.setSelected(true);
        }else {
            hash_map_rb.setSelected(true);
        }
        if (androidx) {
            androidx_rb.setSelected(true);
        } else {
            support_rb.setSelected(true);
        }
        if (nullable) {
            nullable_rb.setSelected(true);
        } else {
            nonnull_rb.setSelected(true);
        }
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
