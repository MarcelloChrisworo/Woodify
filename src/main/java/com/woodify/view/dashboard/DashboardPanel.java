package com.woodify.view.dashboard;

import com.woodify.config.SessionManager;
import com.woodify.model.User;
import com.woodify.view.BasePanel;
import com.woodify.view.MainFrame;

import javax.swing.*;
import java.awt.*;

/**
 * Panel Dashboard — Container for role-based dashboard views.
 */
public class DashboardPanel extends BasePanel {

    private final CardLayout cardLayout;
    private final JPanel cardsPanel;
    
    private AdminDashboardView adminView;
    private CashierDashboardView cashierView;
    private boolean initialized = false;

    public DashboardPanel() {
        super("Dashboard");
        this.cardLayout = new CardLayout();
        this.cardsPanel = new JPanel(cardLayout);
        
        setLayout(new BorderLayout());
        add(cardsPanel, BorderLayout.CENTER);
    }

    private void initViews() {
        if (initialized) return;

        MainFrame mainFrame = (MainFrame) SwingUtilities.getWindowAncestor(this);
        
        adminView = new AdminDashboardView(targetPanel -> {
            if (mainFrame != null) mainFrame.showPanel(targetPanel);
        });
        
        cashierView = new CashierDashboardView(targetPanel -> {
            if (mainFrame != null) mainFrame.showPanel(targetPanel);
        });

        cardsPanel.add(adminView, "ADMIN_VIEW");
        cardsPanel.add(cashierView, "CASHIER_VIEW");
        
        initialized = true;
    }

    @Override
    public void onPageLoad() {
        initViews();
        
        User user = SessionManager.getCurrentUser();
        if (user != null && user.hasAccessToReports()) {
            cardLayout.show(cardsPanel, "ADMIN_VIEW");
            adminView.onPageLoad();
        } else {
            cardLayout.show(cardsPanel, "CASHIER_VIEW");
            cashierView.onPageLoad();
        }
    }
}
