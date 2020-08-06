package edu.ufp.inf.sd.rabbitmqservices.pubsub.chatgui;

import edu.ufp.inf.sd.algorithm.JWT;
import edu.ufp.inf.sd.rmi.server.HashFactoryRI;
import edu.ufp.inf.sd.rmi.server.HashSessionRI;
import edu.ufp.inf.sd.util.rmisetup.SetupContextRMI;

import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoginGui extends javax.swing.JFrame {

    private String[] args;
    private HashFactoryRI hashFactory;
    private SetupContextRMI contextRMI;

    public LoginGui(String[] args) throws RemoteException {
        this.args=args;
        //1. Init the GUI components
        initComponents();
        //2. Init the RMI context (load security manager, lookup subject, etc.)
        initContext(args);
    }

    private void initContext(String[] args) {
        try {
            //List ans set args
            SetupContextRMI.printArgs(this.getClass().getName(), args);
            String registryIP=args[0];
            String registryPort=args[1];
            String serviceName=args[2];
            Logger.getLogger(this.getClass().getName()).log(Level.INFO, "going to setup RMI context...");
            //Create a context for RMI setup
            contextRMI=new SetupContextRMI(this.getClass(), registryIP, registryPort, new String[]{serviceName});
            //Lookup service
            this.hashFactory=(HashFactoryRI) lookupService();

        } catch (RemoteException e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, e);
        }
    }

    private Remote lookupService() {
        try {
            //Get proxy to rmiregistry
            Registry registry = contextRMI.getRegistry();
            //Lookup service on rmiregistry and wait for calls
            if (registry != null) {
                //Get service url (including servicename)
                String serviceUrl = contextRMI.getServicesUrl(0);
                Logger.getLogger(this.getClass().getName()).log(Level.INFO, "going to lookup service @ {0}", serviceUrl);

                //============ Get proxy to HelloWorld service ============
                hashFactory = (HashFactoryRI) registry.lookup(serviceUrl);
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.INFO, "registry not bound (check IPs). :(");
                //registry = LocateRegistry.createRegistry(1099);
            }
        } catch (RemoteException | NotBoundException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }
        return hashFactory;
    }

    /**
     * Gui
     */
    private void initComponents() {

        /**
         * Inicializar as variáveis
         */
        jScrollPane1=new javax.swing.JScrollPane();

        jButtonLogin=new javax.swing.JButton();
        jButtonRegistry=new javax.swing.JButton();

        jTextFieldUsername=new javax.swing.JTextField();
        jTextFieldPassword=new javax.swing.JTextField();

        jLabelUserName=new javax.swing.JLabel();
        jLabelPassword=new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        /**
         * Atribuir textos e respetivas funções às labels e butões
         */
        jButtonLogin.setText("Login");
        jButtonLogin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                try {
                    jButtonLoginActionPerformed(evt);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        jButtonRegistry.setText("Registry");
        jButtonRegistry.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonRegistryActionPerformed(evt);
            }
        });

        jTextFieldUsername.setText("username");
        jTextFieldPassword.setText("password");
        jLabelUserName.setText("Username");
        jLabelPassword.setText("Password:");

        /**
         * Parte gráfica
         */
        javax.swing.GroupLayout layout=new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 250, Short.MAX_VALUE)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                                .addComponent(jLabelUserName)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(jTextFieldUsername, javax.swing.GroupLayout.DEFAULT_SIZE, 141, Short.MAX_VALUE)
                                                .addComponent(jLabelPassword)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(jTextFieldPassword, javax.swing.GroupLayout.DEFAULT_SIZE, 141, Short.MAX_VALUE))
                                )
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButtonLogin)
                                .addComponent(jButtonRegistry)

                        )
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 246, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jButtonLogin)
                                        .addComponent(jButtonRegistry)
                                )
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabelUserName)
                                        .addComponent(jTextFieldUsername, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabelPassword)
                                        .addComponent(jTextFieldPassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }

    /**
     * Registar um user
     * @param evt evento relativo ao carregar no butão
     */
    private void jButtonRegistryActionPerformed(java.awt.event.ActionEvent evt) {
        if(!jLabelUserName.getText().isEmpty() || !jLabelPassword.getText().isEmpty()){

            String token = JWT.createJWT("null",jTextFieldUsername.getText(),jTextFieldPassword.getText(),1000000000);
            try {
                System.out.println(hashFactory.registerUser(token));
            } catch (RemoteException e) {
                e.printStackTrace();
            }

        }else {
            System.out.println("Preencha todos os campos");
        }
    }

    /**
     * Fazer login do cliente
     * @param evt evento relativo ao carregar no butão
     * @throws Exception
     */
    private void jButtonLoginActionPerformed(java.awt.event.ActionEvent evt) throws Exception {

        if (!jTextFieldUsername.getText().isEmpty() || !jTextFieldPassword.getText().isEmpty()) {

            String token = JWT.createJWT("null",jTextFieldUsername.getText(),jTextFieldPassword.getText(),1000000000);
            HashSessionRI session= hashFactory.login(token);

            if (session != null){
                GuiClient.main(this.args, session);
                this.setVisible(false);
            }else {
                System.out.println("Tem de se autenticar");
            }
        } else {
            System.out.println("Preencha todos os campos");
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                if (args.length >= 0) {
                    try {
                        new LoginGui(args).setVisible(true);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                } else {
                    System.out.println(LoginGui.class + ": call must have the following args: <rmi_ip> <rmi_port> <rmi_service_prefix>");
                }
            }
        });
    }

    /**
     * Declarar as variáveis
     */
    private javax.swing.JLabel jLabelUserName;
    private javax.swing.JLabel jLabelPassword;
    private javax.swing.JTextField jTextFieldUsername;
    private javax.swing.JTextField jTextFieldPassword;
    private javax.swing.JButton jButtonLogin;
    private javax.swing.JButton jButtonRegistry;

    private javax.swing.JScrollPane jScrollPane1;
}