/*
 * Copyright (c) 2026, DangGucci
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.partysmarty.ui;

import com.partysmarty.PartySmartyConfig;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Window;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import javax.inject.Inject;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.text.JTextComponent;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.PluginPanel;

public class PartySmartyPanel extends PluginPanel
{
	private static final String AUTO_REJOIN = "Auto rejoin";
	private static final String PARTY_PASSPHRASE = "Party passphrase";

	private final ConfigManager configManager;
	private final JCheckBox autoRejoin;
	private final JTextField passphrase;
	private boolean syncing;

	@Inject
	private PartySmartyPanel(ConfigManager configManager)
	{
		super(false);
		this.configManager = configManager;

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setBackground(ColorScheme.DARK_GRAY_COLOR);
		setBorder(new EmptyBorder(8, 8, 8, 8));

		JPanel row = new JPanel(new BorderLayout());
		row.setOpaque(false);
		row.setAlignmentX(LEFT_ALIGNMENT);
		row.add(label(AUTO_REJOIN), BorderLayout.CENTER);

		autoRejoin = new JCheckBox();
		autoRejoin.setOpaque(false);
		MarkedCheckIcon.install(autoRejoin);
		autoRejoin.addActionListener(e -> saveAutoRejoin());
		row.add(autoRejoin, BorderLayout.EAST);
		int rowHeight = Math.max(row.getPreferredSize().height, autoRejoin.getPreferredSize().height);
		row.setMaximumSize(new Dimension(Integer.MAX_VALUE, rowHeight));

		JLabel passphraseLabel = label(PARTY_PASSPHRASE);
		passphraseLabel.setBorder(new EmptyBorder(10, 0, 4, 0));

		passphrase = new JTextField();
		passphrase.setFont(textFont());
		passphrase.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
		passphrase.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		passphrase.setCaretColor(ColorScheme.LIGHT_GRAY_COLOR);
		passphrase.setOpaque(true);
		passphrase.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createLineBorder(ColorScheme.DARKER_GRAY_COLOR),
			new EmptyBorder(4, 6, 4, 6)));
		passphrase.setAlignmentX(LEFT_ALIGNMENT);
		passphrase.setMaximumSize(new Dimension(Integer.MAX_VALUE, passphrase.getPreferredSize().height));
		passphrase.addActionListener(e -> commitPassphrase());
		passphrase.addFocusListener(new FocusAdapter()
		{
			@Override
			public void focusLost(FocusEvent e)
			{
				commitPassphrase();
			}
		});

		add(row);
		add(passphraseLabel);
		add(passphrase);
		add(Box.createVerticalGlue());
		load();
	}

	@Override
	public void onActivate()
	{
		load();
	}

	public void load()
	{
		syncing = true;
		autoRejoin.setSelected(Boolean.parseBoolean(configManager.getConfiguration(
			PartySmartyConfig.GROUP, PartySmartyConfig.KEY_AUTO_REJOIN)));
		if (!passphrase.isFocusOwner())
		{
			String saved = configManager.getConfiguration(PartySmartyConfig.GROUP, PartySmartyConfig.KEY_PARTY_PASSPHRASE);
			passphrase.setText(saved == null ? "" : saved);
		}
		syncing = false;
		syncOpenSettings();
	}

	private void saveAutoRejoin()
	{
		if (syncing)
		{
			return;
		}

		configManager.setConfiguration(
			PartySmartyConfig.GROUP,
			PartySmartyConfig.KEY_AUTO_REJOIN,
			Boolean.toString(autoRejoin.isSelected()));
	}

	private void commitPassphrase()
	{
		if (syncing)
		{
			return;
		}

		String value = passphrase.getText().trim();
		String saved = configManager.getConfiguration(PartySmartyConfig.GROUP, PartySmartyConfig.KEY_PARTY_PASSPHRASE);
		if (value.equals(saved == null ? "" : saved))
		{
			return;
		}

		configManager.setConfiguration(PartySmartyConfig.GROUP, PartySmartyConfig.KEY_PARTY_PASSPHRASE, value);
	}

	private void syncOpenSettings()
	{
		boolean selected = autoRejoin.isSelected();
		String pass = passphrase.getText();
		for (Window window : Window.getWindows())
		{
			syncOpenSettings(window, selected, pass);
		}
	}

	private void syncOpenSettings(Component component, boolean selected, String pass)
	{
		if (component == this)
		{
			return;
		}

		if (component instanceof JLabel)
		{
			JLabel label = (JLabel) component;
			Container parent = label.getParent();
			if (parent != null)
			{
				if (AUTO_REJOIN.equals(label.getText()))
				{
					applyCheckbox(parent, selected);
				}
				else if (PARTY_PASSPHRASE.equals(label.getText()))
				{
					applyPassphrase(parent, pass);
				}
			}
		}

		if (component instanceof Container)
		{
			for (Component child : ((Container) component).getComponents())
			{
				syncOpenSettings(child, selected, pass);
			}
		}
	}

	private void applyCheckbox(Container parent, boolean selected)
	{
		for (Component child : parent.getComponents())
		{
			if (child == autoRejoin || !(child instanceof JCheckBox))
			{
				continue;
			}

			JCheckBox box = (JCheckBox) child;
			if (box.isSelected() != selected)
			{
				box.setSelected(selected);
			}
		}
	}

	private void applyPassphrase(Container parent, String pass)
	{
		for (Component child : parent.getComponents())
		{
			if (child == passphrase || !(child instanceof JTextComponent))
			{
				continue;
			}

			JTextComponent field = (JTextComponent) child;
			if (!field.isFocusOwner() && !pass.equals(field.getText()))
			{
				field.setText(pass);
			}
		}
	}

	private static JLabel label(String text)
	{
		JLabel label = new JLabel(text);
		label.setFont(textFont());
		label.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
		label.setAlignmentX(LEFT_ALIGNMENT);
		return label;
	}

	private static Font textFont()
	{
		Font runescape = FontManager.getRunescapeFont();
		return runescape.deriveFont(FontManager.getDefaultFont().getSize2D());
	}

	/**
	 * RuneLite's checkbox icon keeps the grey box, but its check is not visible
	 * on this panel. Draw the theme box, then a light check when it is selected.
	 */
	private static final class MarkedCheckIcon implements Icon
	{
		private final Icon box;

		private MarkedCheckIcon(Icon box)
		{
			this.box = box;
		}

		private static void install(JCheckBox checkbox)
		{
			Icon theme = UIManager.getIcon("CheckBox.icon");
			Icon icon = new MarkedCheckIcon(theme);
			checkbox.setIcon(icon);
			checkbox.setSelectedIcon(icon);
			checkbox.setRolloverIcon(icon);
			checkbox.setRolloverSelectedIcon(icon);
			checkbox.setPressedIcon(icon);
			checkbox.setDisabledIcon(icon);
			checkbox.setDisabledSelectedIcon(icon);
		}

		@Override
		public int getIconWidth()
		{
			return box == null ? 22 : box.getIconWidth();
		}

		@Override
		public int getIconHeight()
		{
			return box == null ? 22 : box.getIconHeight();
		}

		@Override
		public void paintIcon(Component c, Graphics g, int x, int y)
		{
			if (box != null)
			{
				box.paintIcon(c, g, x, y);
			}

			if (!(c instanceof AbstractButton) || !((AbstractButton) c).isSelected())
			{
				return;
			}

			Graphics2D g2 = (Graphics2D) g.create();
			try
			{
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2.setColor(ColorScheme.TEXT_COLOR);
				int w = getIconWidth();
				int h = getIconHeight();
				g2.setStroke(new BasicStroke(Math.max(2f, h / 8f), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
				int x1 = x + w * 3 / 16;
				int y1 = y + h * 8 / 16;
				int x2 = x + w * 7 / 16;
				int y2 = y + h * 12 / 16;
				int x3 = x + w * 13 / 16;
				int y3 = y + h * 4 / 16;
				g2.drawLine(x1, y1, x2, y2);
				g2.drawLine(x2, y2, x3, y3);
			}
			finally
			{
				g2.dispose();
			}
		}
	}
}
