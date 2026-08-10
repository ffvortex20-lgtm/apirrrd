@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    
    // Inicia o agente com sincronização a cada 15 minutos
    VortexNetworkAgent agent = VortexNetworkAgent.getInstance(this);
    agent.start(15);
}
