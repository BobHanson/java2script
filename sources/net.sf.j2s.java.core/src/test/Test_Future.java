package test;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import test.baeldung.doublecolon.Computer;
import test.baeldung.doublecolon.MacbookPro;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class Test_Future extends JFrame {

	Executor executor = (Runnable r) -> {
		new Thread(r).start();
	};
	private JButton b1, b2;
	
	public Test_Future() {
		setLocation(400,200);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		b1 = new JButton("fixed");
		b1.setPreferredSize(new Dimension(120,40));
		b1.addActionListener((ActionEvent e) -> {
			btnAction(b1,true);
		});
		b1.setName("b1");
		add(b1, BorderLayout.NORTH);
		b2 = new JButton("delay");
		b2.setPreferredSize(new Dimension(120,40));
		b2.addActionListener((ActionEvent e) -> {
			btnAction(b2,false);
		});
		add(b2, BorderLayout.SOUTH);
		b2.setName("b2");
		pack();
		setVisible(true);
	}

	ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();

	Color lastColor, lastColor1, lastColor2;

	int nFlash = Integer.MAX_VALUE;
	long t0 = 0;

	private void btnAction(JButton button, boolean isFixed) {
		System.out.println("button pressed");
		if (nFlash < 20 && (button == b1 ? lastColor1 : lastColor2) != null)
			return;
		nFlash = 0;
		t0 = 0;
		Runnable r = new Runnable() {

			@Override
			public void run() {
				if (t0 == 0)
					t0 = System.currentTimeMillis();
				System.out.println(button.getName() + " " + nFlash + " " + ((System.currentTimeMillis() - t0) / 1000.));
				if (nFlash++ >= 20) {
					System.out.println("done");
					lastColor1 = lastColor2 = null;
					exec.shutdown();
				} else {
					Color lc = (button == b1 ? lastColor1 : lastColor2);
					lc = (lc == Color.green ? Color.blue : Color.green);
					button.setForeground(lc);
					if (button == b1)
						lastColor1 = lc;
					else
						lastColor2 = lc;
				}
			}
			
		};
		if (isFixed)
			exec.scheduleAtFixedRate(r, 2000, 500, TimeUnit.MILLISECONDS);
		else
			exec.scheduleWithFixedDelay(r, 2000, 500, TimeUnit.MILLISECONDS);
			
		exec.schedule(() -> {
			button.setBackground(lastColor = (lastColor == Color.gray ? Color.lightGray : Color.gray));
			System.out.println("task complete");
		}, 3000, TimeUnit.MILLISECONDS);

		CompletionStage<String> future = longJob();
		future.thenAccept((value) -> {
			System.out.format("returned with %s%n", value);
		});

		ExecutorService dialogExecutor = Executors.newSingleThreadExecutor();
		Future<?> f = dialogExecutor.submit(() -> {
			System.out.println("dialog runnable 1");
		});
		dialogExecutor.submit(() -> {
			System.out.println("dialog runnable 2");
		});
		dialogExecutor.submit(() -> {
			System.out.println("dialog runnable 3");
			System.out.println(f.toString() + f.isDone());
		});
		System.out.println("CompletionStage started");
		System.out.println(f.toString() + f.isDone());

	}

	CompletableFuture<String> longJob() {
		return CompletableFuture.supplyAsync(this::getValue, executor);
	}

	String getValue() {
		return "Hello";
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(Test_Future::new);
	}
}
