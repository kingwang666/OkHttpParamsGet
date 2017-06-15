package com.wang.okhttpparamsget;

import javax.swing.*;
import java.awt.event.*;

public class TypePickDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JRadioButton string_rb;
    private JRadioButton request_body_rb;
    private JRadioButton part_rb;
    private JRadioButton body_rb;
    private int mType;

    private OnDialogClickListener listener;

    public TypePickDialog() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        mType = 1;
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
                    mType = 1;
                    request_body_rb.setSelected(false);
                    part_rb.setSelected(false);
                    body_rb.setSelected(false);
                }
                else if (mType == 1){
                    string_rb.setSelected(true);
                }
            }
        });

        request_body_rb.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (request_body_rb.isSelected()){
                    mType = 2;
                    string_rb.setSelected(false);
                    part_rb.setSelected(false);
                    body_rb.setSelected(false);
                }
                else if (mType == 2){
                    request_body_rb.setSelected(true);
                }
            }
        });

        part_rb.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (part_rb.isSelected()){
                    mType = 3;
                    request_body_rb.setSelected(false);
                    string_rb.setSelected(false);
                    body_rb.setSelected(false);
                }
                else if (mType == 3){
                    part_rb.setSelected(true);
                }
            }
        });

        body_rb.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (body_rb.isSelected()){
                    mType = 4;
                    request_body_rb.setSelected(false);
                    string_rb.setSelected(false);
                    part_rb.setSelected(false);
                }
                else if (mType == 4){
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
        }
        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    public static void main(String[] args) {
        TypePickDialog dialog = new TypePickDialog();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }
}
