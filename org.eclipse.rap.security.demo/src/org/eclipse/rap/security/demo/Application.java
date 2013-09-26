package org.eclipse.rap.security.demo;

import java.net.URL;
import java.security.PrivilegedAction;

import javax.security.auth.Subject;
import javax.security.auth.login.LoginException;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.equinox.security.auth.ILoginContext;
import org.eclipse.equinox.security.auth.LoginContextFactory;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.osgi.framework.BundleContext;

/**
 * This class controls all aspects of the application's execution and is
 * contributed through the plugin.xml.
 */
public class Application implements IApplication {
	
	private static final String JAAS_CONFIG_FILE = "data/jaas_config.txt";

	@SuppressWarnings("unchecked")
	public Object start(IApplicationContext context) throws Exception {
		// String configName = "KeyStore";
		// String configName = "UNIX";
		String configName = "DUMMY";
		BundleContext bundleContext = Activator.getBundleContext();
		URL configUrl = bundleContext.getBundle().getEntry(JAAS_CONFIG_FILE);
		ILoginContext secureContext = LoginContextFactory.createContext(
				configName, configUrl);
		try {
			secureContext.login();
		} catch (LoginException exception) {
			Throwable cause = exception.getCause();
			if (cause != null && cause.getCause() instanceof ThreadDeath) {
				throw (ThreadDeath) cause.getCause();
			}
			IStatus status = new Status(IStatus.ERROR,
					"org.eclipse.rap.security.demo", "Login failed", exception);
			ErrorDialog.openError(null, "Error", "Login failed", status);
		}
		Integer result = null;
		Display display = PlatformUI.createDisplay();
		try {
			result = (Integer) Subject.doAs(secureContext.getSubject(),
					getRunAction(display));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			display.dispose();
			secureContext.logout();
		}
		return result;
	}

	@SuppressWarnings("rawtypes")
	private PrivilegedAction getRunAction(final Display display) {
		return new PrivilegedAction() {

			public Object run() {
				int result = PlatformUI.createAndRunWorkbench(display,
						new ApplicationWorkbenchAdvisor());
				return new Integer(result);
			}
		};
	}

	public void stop() {
		final IWorkbench workbench = PlatformUI.getWorkbench();
		if (workbench != null) {
			final Display display = workbench.getDisplay();
			display.syncExec(new Runnable() {
				public void run() {
					if (!display.isDisposed())
						workbench.close();
				}
			});
		}
	}
}
