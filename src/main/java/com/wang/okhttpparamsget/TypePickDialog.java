package com.wang.okhttpparamsget;

import javax.swing.*;
import java.awt.event.*;

public class TypePickDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JRadioButton string_rb;
    private JRadioButton object_rb;
    private JRadioButton request_body_rb;
    private JRadioButton part_rb;
    private JRadioButton body_rb;
    private int mType;

    private OnDialogClickListener listener;

    public TypePickDialog() {
        setContentPane(contentPane);
        setModal(true);
        setTitle("Params");
        getRootPane().setDefaultButton(buttonOK);
        mType = Constant.TYPE_MAP_STRING;
        string_rb.setSelected(true);
        initRadioClickListener();
        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });


        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

                // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        setLocationRelativeTo(null);
    }

    private void initRadioClickListener(){
        string_rb.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (string_rb.isSelected()){
                    mType = Constant.TYPE_MAP_STRING;
                    object_rb.setSelected(false);
                    request_body_rb.setSelected(false);
                    part_rb.setSelected(false);
                    body_rb.setSelected(false);
                }
                else if (mType == Constant.TYPE_MAP_STRING){
                    string_rb.setSelected(true);
                }
            }
        });

        object_rb.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (object_rb.isSelected()){
                    mType = Constant.TYPE_MAP_OBJECT;
                    string_rb.setSelected(false);
                    request_body_rb.setSelected(false);
                    part_rb.setSelected(false);
                    body_rb.setSelected(false);
                }
                else if (mType == Constant.TYPE_MAP_OBJECT){
                    object_rb.setSelected(true);
                }
            }
        });

        request_body_rb.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (request_body_rb.isSelected()){
                    mType = Constant.TYPE_MAP_BODY;
                    string_rb.setSelected(false);
                    object_rb.setSelected(false);
                    part_rb.setSelected(false);
                    body_rb.setSelected(false);
                }
                else if (mType == Constant.TYPE_MAP_BODY){
                    request_body_rb.setSelected(true);
                }
            }
        });

        part_rb.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (part_rb.isSelected()){
                    mType = Constant.TYPE_LIST_PART;
                    request_body_rb.setSelected(false);
                    string_rb.setSelected(false);
                    body_rb.setSelected(false);
                    object_rb.setSelected(false);
                }
                else if (mType == Constant.TYPE_LIST_PART){
                    part_rb.setSelected(true);
                }
            }
        });

        body_rb.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (body_rb.isSelected()){
                    mType = Constant.TYPE_BODY_BUILDER;
                    request_body_rb.setSelected(false);
                    string_rb.setSelected(false);
                    object_rb.setSelected(false);
                    part_rb.setSelected(false);
                }
                else if (mType == Constant.TYPE_BODY_BUILDER){
                    body_rb.setSelected(true);
                }
            }
        });
    }

    public OnDialogClickListener getListener() {
        return listener;
    }

    public void setListener(OnDialogClickListener listener) {
        this.listener = listener;
    }

    private void onOK() {
        // add your code here
        if (listener != null){
            listener.onClickOk(mType);
            listener = null;
        }
        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        listener = null;
        dispose();
    }

    @Override
    public void dispose() {
        super.dispose();
    }

    public static void main(String[] args) {
        TypePickDialog dialog = new TypePickDialog();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }
}
