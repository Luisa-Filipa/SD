package edu.ufp.inf.sd.rabbitmqservices.pubsub.chatgui;

import edu.ufp.inf.sd.rmi.server.HashSessionRI;

import javax.swing.*;
import java.io.IOException;
import java.rmi.RemoteException;

import static java.lang.Integer.parseInt;

public class GuiClientEst1 extends JFrame {

    private String[] args;
    private HashSessionRI hashSessionRI;
    private GuiClient guiClient;

    public GuiClientEst1(String[] args, HashSessionRI hashSessionRI,GuiClient guiClient) throws RemoteException {
        //1. Init the GUI components
        initComponents();
        this.args=args;
        this.hashSessionRI= hashSessionRI;
        this.guiClient=guiClient;
    }

    /**
     * Gui
     */
    private void initComponents() {

        /**
         * Inicializar as variáveis
         */
        jScrollPane1=new JScrollPane();

        jButtonCreate=new JButton();

        jTextFieldFile= new JTextField();
        jTextFieldHashAlgorithm=new JTextField();
        jTextFieldCreditos=new JTextField();
        jTextFieldHash=new JTextField();

        jLabelFile=new JLabel();
        jLabelFieldHashAlgorithm=new JLabel();
        jLabelFieldCreditos=new JLabel();
        jLabelFieldHash=new JLabel();

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        jButtonCreate.setText("Create");
        jButtonCreate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                try {
                    jButtonCreateActionPerformed(evt);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });

        jLabelFile.setText("URL:");
        jLabelFieldHashAlgorithm.setText("Algorithm:");
        jLabelFieldCreditos.setText("Creditos:");
        jLabelFieldHash.setText("Hash:");

        jTextFieldFile.setText("https://raw.githubusercontent.com/Luisa-Filipa/teste/master/teste.txt");
        jTextFieldCreditos.setText("20");
        jTextFieldHash.setText("$2y$12$rs6cyjt38ckdRyfpq0EM/.ziLPGgjzUa9hSE8quBG3gSHlNKuUvTy");
        jTextFieldHashAlgorithm.setText("BCrypt");

        /**
         * Parte gráfica
         */
        GroupLayout layout=new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(jScrollPane1, GroupLayout.DEFAULT_SIZE, 250, Short.MAX_VALUE)
                        .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                                .addComponent(jLabelFieldCreditos, GroupLayout.DEFAULT_SIZE, 141, Short.MAX_VALUE)
                                                .addComponent(jTextFieldCreditos)
                                                .addComponent(jLabelFile, GroupLayout.DEFAULT_SIZE, 141, Short.MAX_VALUE)
                                                .addComponent(jTextFieldFile)
                                                .addComponent(jLabelFieldHashAlgorithm, GroupLayout.DEFAULT_SIZE, 141, Short.MAX_VALUE)
                                                .addComponent(jTextFieldHashAlgorithm)
                                                .addComponent(jLabelFieldHash, GroupLayout.DEFAULT_SIZE, 141, Short.MAX_VALUE)
                                                .addComponent(jTextFieldHash)
                                                .addComponent(jButtonCreate)
                                                .addGroup(GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED))
                                )
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                /*.addComponent(jButtonCreate)*/

                        )
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(jScrollPane1, GroupLayout.PREFERRED_SIZE, 246, GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabelFieldCreditos, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addComponent(jTextFieldCreditos)
                                .addComponent(jLabelFile, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addComponent(jTextFieldFile)
                                .addComponent(jLabelFieldHashAlgorithm, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addComponent(jTextFieldHashAlgorithm)
                                .addComponent(jLabelFieldHash, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addComponent(jTextFieldHash)
                                .addComponent(jButtonCreate))

                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE))
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE))
        );
        pack();
    }

    /**
     * @param evt evento relativo ao carregar no butão
     * @throws Exception
     */
    private void jButtonCreateActionPerformed(java.awt.event.ActionEvent evt) throws RemoteException {
        if (!jTextFieldHashAlgorithm.getText().isEmpty() && !jTextFieldFile.getText().isEmpty() && !jTextFieldCreditos.getText().isEmpty() && !jTextFieldHash.getText().isEmpty()) {
            if (parseInt(jTextFieldCreditos.getText())<12 ){
                JOptionPane.showMessageDialog(null, "Numero de creditos inválido (minimo 12)");
            }
            else if((jTextFieldHashAlgorithm.getText()).compareTo("BCrypt")!=0 && jTextFieldHashAlgorithm.getText().compareTo("SHA_512")!=0){
                JOptionPane.showMessageDialog(null, "Algoritmo desconhecido (BCrypt,SHA_512)");
            }
            else if (hashSessionRI.RemoveCredits(parseInt(jTextFieldCreditos.getText()))){
                try {
                    hashSessionRI.createTaskGroup("", jTextFieldFile.getText(), jTextFieldHashAlgorithm.getText(), jTextFieldCreditos.getText(), jTextFieldHash.getText(), "0", "0", "all", "0");
                    int credits = parseInt(this.hashSessionRI.getCredits());
                    this.guiClient.updateCredits(Integer.toString(credits));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else {
                JOptionPane.showMessageDialog(null, "Creditos insuficientes");
            }
        } else {
            JOptionPane.showMessageDialog(null, "Tem de preencher todos os campos");
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args, HashSessionRI hashSessionRI,GuiClient guiClient) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                if (args.length >= 0) {
                    try {
                        new GuiClientEst1(args,hashSessionRI,guiClient).setVisible(true);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                } else {
                    System.out.println(GuiClientEst1.class + ": call must have the following args: <rmi_ip> <rmi_port> <rmi_service_prefix>");
                }
            }
        });
    }

    /**
     * Declarar as variáveis
     */

    private JLabel jLabelFile;
    private JLabel jLabelFieldHashAlgorithm;
    private JLabel jLabelFieldCreditos;
    private JLabel jLabelFieldHash;


    private JTextField jTextFieldFile;
    private JTextField jTextFieldHashAlgorithm;
    private JTextField jTextFieldHash;
    private JTextField jTextFieldCreditos;

    private JButton jButtonCreate;

    private JScrollPane jScrollPane1;
}
