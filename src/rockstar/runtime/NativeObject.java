package rockstar.runtime;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Objects;

public class NativeObject {

	private Class<?> nativeClass;
	private boolean isStaticInstance;
	private Object nativeObject;

	private NativeObject(Class<?> nativeClass, boolean isStaticInstance, Object nativeObject) {
		this.nativeClass = nativeClass;
		this.isStaticInstance = isStaticInstance;
		this.nativeObject = nativeObject;
	}

	public static NativeObject getStatic(QualifiedClassName qcn) {
		Class<?> nativeClass = getClassForName(qcn);
		return new NativeObject(nativeClass, true, null);
	}

	public static NativeObject construct(QualifiedClassName qcn, List<Value> ctorParams) {
		Class<?> nativeClass = getClassForName(qcn);
		Class<?>[] types = convertTypes(ctorParams);
		Constructor<?> ctor = getConstructor(nativeClass, types);
		Object[] initArgs = convertValues(ctor.getParameterTypes(), ctorParams);
		try {
			Object nativeObject = ctor.newInstance(initArgs);
			return new NativeObject(nativeClass, false, nativeObject);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			throw new RockstarRuntimeException("Cannot instantiate native class: "+ qcn.getJavaClassName() + " with params " + ctorParams);
		}
	}
	
	public Class<?> getNativeClass() {
		return nativeClass;
	}

	public boolean isStaticInstance() {
		return isStaticInstance;
	}

	public Object getNativeObject() {
		return nativeObject;
	}

	public String describe() {
		if (isStaticInstance) {
			return nativeClass.getCanonicalName();
		}
		return nativeObject != null ? nativeObject.toString() : "";
	}

	public boolean getBool() {
		return isStaticInstance || (nativeObject != null);
	}

	@Override
	public String toString() {
		return (isStaticInstance ? "Class " : "") + nativeClass.getCanonicalName()
				+ (nativeObject != null ? " " + nativeObject.toString() : (!isStaticInstance ? " <null>" : ""));
	}
	
	@Override
	public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof NativeObject) {
        	NativeObject o = (NativeObject) obj;
        	return nativeClass.equals(o.nativeClass)
        			&& isStaticInstance == o.isStaticInstance
        			&& Objects.equals(nativeObject, o.nativeObject);
        }
        return false;
	}

	////////////////////
	// Java bindings
	
	private static Class<?> getClassForName(QualifiedClassName qcn) {
		try {
			return Class.forName(qcn.getJavaClassName());
		} catch (ClassNotFoundException e) {
			throw new RockstarRuntimeException("Cannot find native class: "+ qcn.getJavaClassName());
		}
	}

	private static Class<?>[] convertTypes(List<Value> values) {
		Class<?>[] args = new Class<?>[values.size()];
		int i = 0;
		for (Value value : values) {
			args[i++] = convertType(value); 
		}
		return args;
	}
	
	private static Class<?> convertType(Value value) {
		if (value.isNative()) {
			return value.getNative().getNativeClass();
		} else if (value.isString()) {
			return String.class;
		}
		return null;
	}

	private static Object[] convertValues(Class<?>[] targetClasses, List<Value> values) {
		Object[] args = new Object[values.size()];
		int i = 0;
		for (Value value : values) {
			args[i++] = convertValue(targetClasses[i], value); 
		}
		return args;
	}
	
	private static Object convertValue(Class<?> targetClass, Value value) {
		// TODO convert values to required types
		if (value.isNative()) {
			return value.getNative().getNativeObject();
		} else if (targetClass.equals(String.class)) {
			return value.getString();
		} else if (targetClass.equals(Double.class)) {
			return value.getNumeric().asDouble();
		}
		return null;
	}

	private static Constructor<?> getConstructor(Class<?> nativeClass, Class<?>[] types) {
		Constructor<?>[] ctors = nativeClass.getConstructors();
		for (int i = 0; i < ctors.length; i++) {
			Constructor<?> constructor = ctors[i];
			if (matchParameterTypes(constructor.getParameterTypes(), types)) {
				return constructor;
			}
		}
		return null;
	}

	private static boolean matchParameterTypes(Class<?>[] parameterTypes, Class<?>[] types) {
		// TODO Auto-generated method stub
		return false;
	}

	public Value callMethod(String functionName, List<Value> values) {
		// try function name
		
		// try property name
		
		
		return null;
	}
	
	

}
