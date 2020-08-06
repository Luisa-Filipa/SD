package edu.ufp.inf.sd.rabbitmqservices.pubsub.chatgui;

import edu.ufp.inf.sd.rmi.server.HashSessionRI;
import edu.ufp.inf.sd.rmi.server.TaskGroupRI;

import javax.swing.*;
import java.rmi.RemoteException;

import static java.lang.Integer.parseInt;

public class AdicionarCreditos extends JFrame  {

        private String[] args;
        private HashSessionRI hashSessionRI;

        public AdicionarCreditos(String[] args, HashSessionRI hashSessionRI) throws RemoteException {
            //1. Init the GUI components
            initComponents();
            this.args=args;
            this.hashSessionRI= hashSessionRI;
        }

        /**
         * Gui
         */
        private void initComponents() {

            /**
             * Inicializar as variáveis
             */
            jScrollPane1=new JScrollPane();

            jButtonAddCredits =new JButton();

            jTextFieldCreditos=new JTextField();
            jLabelFieldCreditos=new JLabel();
            jLabelFieldCreditos.setText("Créditos");

            jTextFieldTaskGroup=new JTextField();
            jLabelFieldTaskGroup=new JLabel();
            jLabelFieldTaskGroup.setText("ID da TaskGroup");

            setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

            jButtonAddCredits.setText("Add");
            jButtonAddCredits.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    try {
                        jButtonAddCreditsActionPerformed(evt);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            });

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
                                                    .addComponent(jLabelFieldTaskGroup, GroupLayout.DEFAULT_SIZE, 141, Short.MAX_VALUE)
                                                    .addComponent(jTextFieldTaskGroup)
                                                    .addComponent(jButtonAddCredits)
                                                    .addGroup(GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                                    .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED))
                                            )
                                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            )
            );
            layout.setVerticalGroup(
                    layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                    .addComponent(jScrollPane1, GroupLayout.PREFERRED_SIZE, 246, GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabelFieldCreditos, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jTextFieldCreditos)
                                    .addComponent(jLabelFieldTaskGroup, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jTextFieldTaskGroup)
                                    .addComponent(jButtonAddCredits)
                                    )
                            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE))
                            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE))
            );
            pack();
        }

        /**
         * Fazer login do cliente
         * @param evt evento relativo ao carregar no butão
         * @throws Exception
         */
        private void jButtonAddCreditsActionPerformed(java.awt.event.ActionEvent evt) throws RemoteException {
            if (!jTextFieldCreditos.getText().isEmpty() || !jTextFieldTaskGroup.getText().isEmpty()) {
                if (parseInt(jTextFieldCreditos.getText())<10){
                    JOptionPane.showMessageDialog(null, "Numero de creditos inválido");
                }else{
                    hashSessionRI.setCreditsToTaskGroup(parseInt(jTextFieldCreditos.getText()),jTextFieldTaskGroup.getText());
                }
            }else {
                JOptionPane.showMessageDialog(null, "Tem de preencher todos os campos");
            }
        }

        /**
         * @param args the command line arguments
         */
        public static void main(String[] args, HashSessionRI hashSessionRI) {
            java.awt.EventQueue.invokeLater(new Runnable() {

                @Override
                public void run() {
                    if (args.length >= 0) {
                        try {
                           new AdicionarCreditos(args,hashSessionRI).setVisible(true);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    } else {
                        System.out.println(this + ": call must have the following args: <rmi_ip> <rmi_port> <rmi_service_prefix>");
                    }
                }
            });
        }

        /**
         * Declarar as variáveis
         */


        private JLabel jLabelFieldCreditos;

        private JTextField jTextFieldCreditos;

        private JTextField jTextFieldTaskGroup;

        private JLabel jLabelFieldTaskGroup;

        private JButton jButtonAddCredits;

        private JScrollPane jScrollPane1;
    }