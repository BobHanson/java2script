package java.security;

public class AccessController implements AccessControlContext {

	// a dummy class

	public static <T> T doPrivileged(PrivilegedAction<T> action) {
		return action.run();
	}

	public static <T> T doPrivileged(PrivilegedExceptionAction<T> action) throws PrivilegedActionException{
		try {
			return action.run();
		} catch (Exception e) {
		}
		return null;
	}


	
	public static <T> T doPrivileged(PrivilegedAction<T> action, AccessControlContext context) {
		return action.run();
	}

	public static AccessControlContext getContext() {
		return new AccessController();
	}

	@Override
	public boolean checkPermission(Object perm) {
		// no access checking in JavaScript
		return true;
	}

}
