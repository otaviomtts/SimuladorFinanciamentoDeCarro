package br.com.concessionaria;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.text.NumberFormat;
import java.util.Locale;

public class FinanciamentoCarros extends JFrame {

    private static final double TAXA = 0.32;

    private static final NumberFormat MOEDA =
            NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

    private final JComboBox<String> cbMarca = new JComboBox<>(new String[]{
            "Selecione...", "FIAT", "VOLKSWAGEN", "CHEVROLET", "FORD", "TOYOTA",
            "HONDA", "HYUNDAI", "RENAULT", "JEEP", "NISSAN"});
    private final JTextField txtModelo = new JTextField(20);
    private final JComboBox<Integer> cbAno = new JComboBox<>();
    private final JTextField txtValor = new JTextField(20);

    private final JRadioButton rbNovo = new JRadioButton("NOVO", true);
    private final JRadioButton rbUsado = new JRadioButton("USADO");

    private final JPanel pnlUsado = new JPanel(new GridBagLayout());
    private final JTextField txtKm = new JTextField(18);
    private final JTextField txtProprietarios = new JTextField(18);

    private final JCheckBox chkEntrada = new JCheckBox("Possui entrada?");
    private final JLabel lblEntrada = new JLabel("Entrada");
    private final JTextField txtEntrada = new JTextField(20);
    private final JComboBox<Integer> cbParcelas =
            new JComboBox<>(new Integer[]{12, 24, 36, 48, 60});

    private final JPanel pnlResultado = new JPanel(new GridLayout(3, 1, 0, 6));
    private final JLabel lblFinanciado = new JLabel();
    private final JLabel lblParcela = new JLabel();
    private final JLabel lblTotal = new JLabel();

    public FinanciamentoCarros() {
        super("Financiamento de Carros");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        for (int ano = 2026; ano >= 2000; ano--) {
            cbAno.addItem(ano);
        }
        cbParcelas.setSelectedItem(36);

        setLayout(new BorderLayout(10, 10));

        JLabel titulo = new JLabel("Financiamento de Carros", SwingConstants.CENTER);
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 18f));
        titulo.setBorder(new EmptyBorder(10, 0, 0, 0));
        add(titulo, BorderLayout.NORTH);

        JPanel conteudo = new JPanel();
        conteudo.setLayout(new BoxLayout(conteudo, BoxLayout.Y_AXIS));
        conteudo.setBorder(new EmptyBorder(5, 15, 15, 15));
        conteudo.add(criarPainelVeiculo());
        conteudo.add(criarPainelTipo());
        conteudo.add(criarPainelUsado());
        conteudo.add(criarPainelFinanciamento());
        conteudo.add(criarPainelBotoes());
        conteudo.add(criarPainelResultado());
        JPanel topo = new JPanel(new BorderLayout());
        topo.add(conteudo, BorderLayout.NORTH);
        JScrollPane scroll = new JScrollPane(topo);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);

        configurarEventos();
        atualizarVisibilidade();

        pack();
        setMinimumSize(new Dimension(320, 400));
        setLocationRelativeTo(null);
    }


    private JPanel criarPainelVeiculo() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBorder(new TitledBorder("Dados do Veículo"));
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        adicionarLinha(p, 0, "Marca", cbMarca);
        adicionarLinha(p, 1, "Modelo", txtModelo);
        adicionarLinha(p, 2, "Ano", cbAno);
        adicionarLinha(p, 3, "Valor", txtValor);
        return p;
    }

    private JPanel criarPainelTipo() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        ButtonGroup grupo = new ButtonGroup();
        grupo.add(rbNovo);
        grupo.add(rbUsado);
        p.add(new JLabel("Tipo"));
        p.add(rbNovo);
        p.add(rbUsado);
        return p;
    }

    private JPanel criarPainelUsado() {
        pnlUsado.setBorder(new TitledBorder("Dados do Veículo Usado"));
        pnlUsado.setAlignmentX(Component.LEFT_ALIGNMENT);
        adicionarLinha(pnlUsado, 0, "Quilometragem", txtKm);
        adicionarLinha(pnlUsado, 1, "Proprietários", txtProprietarios);
        return pnlUsado;
    }

    private JPanel criarPainelFinanciamento() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBorder(new TitledBorder("Financiamento"));
        p.setAlignmentX(Component.LEFT_ALIGNMENT);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 6, 4, 6);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        p.add(chkEntrada, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 1; gbc.gridx = 0;
        p.add(lblEntrada, gbc);
        gbc.gridx = 1; gbc.weightx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        p.add(txtEntrada, gbc);

        gbc.gridy = 2; gbc.gridx = 0; gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE;
        p.add(new JLabel("Parcelas"), gbc);
        gbc.gridx = 1; gbc.weightx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        p.add(cbParcelas, gbc);
        return p;
    }

    private JPanel criarPainelBotoes() {
        // GridLayout: os dois botões dividem a largura disponível igualmente
        JPanel p = new JPanel(new GridLayout(1, 2, 15, 0));
        p.setBorder(new EmptyBorder(10, 0, 10, 0));
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        JButton btnCalcular = new JButton("CALCULAR");
        JButton btnLimpar = new JButton("LIMPAR");
        btnCalcular.addActionListener(e -> calcular());
        btnLimpar.addActionListener(e -> limpar());
        p.add(btnCalcular);
        p.add(btnLimpar);
        return p;
    }

    private JPanel criarPainelResultado() {
        pnlResultado.setBorder(new TitledBorder("Resultado"));
        pnlResultado.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlResultado.add(lblFinanciado);
        pnlResultado.add(lblParcela);
        pnlResultado.add(lblTotal);
        pnlResultado.setVisible(false);
        return pnlResultado;
    }

    private void adicionarLinha(JPanel painel, int linha, String texto, JComponent campo) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 6, 4, 6);
        gbc.gridy = linha;

        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.WEST;
        painel.add(new JLabel(texto), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        painel.add(campo, gbc);
    }


    private void configurarEventos() {
        rbNovo.addActionListener(e -> atualizarVisibilidade());
        rbUsado.addActionListener(e -> atualizarVisibilidade());
        chkEntrada.addActionListener(e -> atualizarVisibilidade());
    }

    private void atualizarVisibilidade() {
        pnlUsado.setVisible(rbUsado.isSelected());
        lblEntrada.setVisible(chkEntrada.isSelected());
        txtEntrada.setVisible(chkEntrada.isSelected());
        ajustarTamanho();
    }

    private void ajustarTamanho() {
        revalidate();
        Dimension pref = getPreferredSize();
        Dimension tela = Toolkit.getDefaultToolkit().getScreenSize();
        int w = Math.max(getWidth(), Math.min(pref.width, tela.width));
        int h = Math.max(getHeight(), Math.min(pref.height, tela.height - 60));
        setSize(w, h);
        repaint();
    }

    private void limpar() {
        cbMarca.setSelectedIndex(0);
        txtModelo.setText("");
        cbAno.setSelectedIndex(0);
        txtValor.setText("");
        rbNovo.setSelected(true);
        txtKm.setText("");
        txtProprietarios.setText("");
        chkEntrada.setSelected(false);
        txtEntrada.setText("");
        cbParcelas.setSelectedItem(36);
        pnlResultado.setVisible(false);
        atualizarVisibilidade();
    }

    // ------------------------------------------------------------------ cálculo

    private void calcular() {
        pnlResultado.setVisible(false);

        if (cbMarca.getSelectedIndex() == 0) {
            erro("Selecione a marca do veículo.", cbMarca);
            return;
        }
        if (txtModelo.getText().trim().isEmpty()) {
            erro("Informe o modelo do veículo.", txtModelo);
            return;
        }
        Double valor = lerNumero(txtValor.getText());
        if (valor == null || valor <= 0) {
            erro("Informe um valor válido (maior que zero) para o veículo.", txtValor);
            return;
        }

        if (rbUsado.isSelected()) {
            Double km = lerNumero(txtKm.getText());
            if (km == null || km < 0) {
                erro("Informe uma quilometragem válida.", txtKm);
                return;
            }
            try {
                int prop = Integer.parseInt(txtProprietarios.getText().trim());
                if (prop < 1) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                erro("Informe um número de proprietários válido (mínimo 1).", txtProprietarios);
                return;
            }
        }

        double entrada = 0;
        if (chkEntrada.isSelected()) {
            Double e = lerNumero(txtEntrada.getText());
            if (e == null || e < 0) {
                erro("Informe um valor de entrada válido.", txtEntrada);
                return;
            }
            if (e >= valor) {
                erro("A entrada deve ser menor que o valor do veículo.", txtEntrada);
                return;
            }
            entrada = e;
        }

        int parcelas = (Integer) cbParcelas.getSelectedItem();
        double valorFinanciado = valor - entrada;
        double valorTotal = valorFinanciado * (1 + TAXA);
        double valorParcela = valorTotal / parcelas;
        double totalPagar = valorParcela * parcelas;

        lblFinanciado.setText("Valor financiado: " + MOEDA.format(valorFinanciado));
        lblParcela.setText("Valor da parcela: " + MOEDA.format(valorParcela));
        lblTotal.setText("Total a pagar: " + MOEDA.format(totalPagar));
        pnlResultado.setVisible(true);
        ajustarTamanho();
    }

    private Double lerNumero(String texto) {
        String t = texto.replace("R$", "").replace(" ", "").trim();
        if (t.isEmpty()) return null;
        if (t.contains(",")) {
            t = t.replace(".", "").replace(",", ".");
        }
        try {
            return Double.parseDouble(t);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private void erro(String mensagem, JComponent foco) {
        JOptionPane.showMessageDialog(this, mensagem, "Validação", JOptionPane.WARNING_MESSAGE);
        foco.requestFocusInWindow();
    }

    // --------------------------------------------------------------------- main

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FinanciamentoCarros().setVisible(true));
    }
}
