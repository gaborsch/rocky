package rockstar.runtime;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
		if (nativeClass == null) {
			return null;
		}
		return new NativeObject(nativeClass, true, null);
	}


	public NativeObject newInstance(List<Value> ctorParams) {
		Constructor<?> ctor = getConstructor(nativeClass, ctorParams);
		Object[] initArgs = convertValues(ctor, ctorParams);
		try {
			Object nativeObject = ctor.newInstance(initArgs);
			return new NativeObject(nativeClass, false, nativeObject);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			throw new RockstarRuntimeException("Cannot instantiate native class: "+ nativeClass.getCanonicalName() + " with params " + ctorParams);
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
			return null;
		}
	}

	private static Constructor<?> getConstructor(Class<?> nativeClass, List<Value> ctorParams) {
		Constructor<?>[] ctors = nativeClass.getConstructors();
		for (int i = 0; i < ctors.length; i++) {
			Constructor<?> constructor = ctors[i];
			if (matchParameterTypes(constructor.getParameterTypes(), ctorParams)) {
				return constructor;
			}
		}
		return null;
	}

	private static boolean matchParameterTypes(Class<?>[] parameterTypes, List<Value> ctorParams) {
		if (parameterTypes.length != ctorParams.size()) {
			return false;
		}
		int i = 0;
		for (Value value : ctorParams) {
			if (! matchParameterType(parameterTypes[i++], value)) {
				return false;
			}
		}
		return true;
	}
	
	private static final Class<?>[] LONG_CLASSES = new Class<?>[] { long.class, Long.class };

	private static final Class<?>[] INT_CLASSES = new Class<?>[] { int.class, Integer.class };

	private static final Class<?>[] DOUBLE_CLASSES = new Class<?>[] { double.class, Double.class };

	private static final Class<?>[] CHAR_CLASSES = new Class<?>[] { char.class, Character.class };

	private static final Class<?>[] SHORT_CLASSES = new Class<?>[] { short.class, Short.class };

	private static final Class<?>[] BYTE_CLASSES = new Class<?>[] { byte.class, Byte.class };

	private static final Class<?>[] FLOAT_CLASSES = new Class<?>[] { float.class, Float.class };

	private static final Class<?>[] OTHER_NUMERIC_CLASSES = new Class<?>[] { BigDecimal.class, BigInteger.class };

	private static final Class<?>[] ARRAY_CLASSES = new Class<?>[] { List.class, Map.class };

	private static boolean matchParameterType(Class<?> cls, Value value) {
		switch (value.getType()) {
		case NATIVE:
			return isAssignableFrom(cls, value.getNative().getNativeClass());
		case NUMBER:
			return isAssignableFrom(cls, LONG_CLASSES) 
					|| isAssignableFrom(cls, INT_CLASSES) 
					|| isAssignableFrom(cls, SHORT_CLASSES) 
					|| isAssignableFrom(cls, BYTE_CLASSES) 
					|| isAssignableFrom(cls, CHAR_CLASSES) 
					|| isAssignableFrom(cls, DOUBLE_CLASSES) 
					|| isAssignableFrom(cls, FLOAT_CLASSES)
					|| isAssignableFrom(cls, OTHER_NUMERIC_CLASSES);
		case STRING:
			return isAssignableFrom(cls, String.class);
		case ARRAY:
			return cls.isArray() || isAssignableFrom(cls, ARRAY_CLASSES);
		case BOOLEAN:
			return isAssignableFrom(cls, boolean.class, Boolean.class);
		case NULL:
			return !cls.isPrimitive();
		case MYSTERIOUS:
			return false;
		case OBJECT:
			return false;
		default:
			return false;
		}
	}
	
	private static Object[] convertValues(Constructor<?> ctor, List<Value> values) {
		Class<?>[] ctorTypes = ctor.getParameterTypes();
		Object[] args = new Object[values.size()];
		int i = 0;
		for (Value value : values) {
			args[i++] = convertValue(ctorTypes[i], value); 
		}
		return args;
	}
	
	private static Object convertValue(Class<?> cls, Value value) {
		switch(value.getType()) {
			case NATIVE:
				return value.getNative().getNativeObject();
			case NUMBER:
				if (isAssignableFrom(cls, LONG_CLASSES)) {
					return (Long) value.getNumeric().asLong();
				} else if (isAssignableFrom(cls, INT_CLASSES)) {
					return value.getNumeric().asInt();
				} else if (isAssignableFrom(cls, SHORT_CLASSES)) {
					return (short)value.getNumeric().asInt();
				} else if (isAssignableFrom(cls, BYTE_CLASSES)) {
					return (byte)value.getNumeric().asInt();
				} else if (isAssignableFrom(cls, CHAR_CLASSES)) {
					return (char)value.getNumeric().asInt();
				} else if (isAssignableFrom(cls, DOUBLE_CLASSES)) {
					return value.getNumeric().asDouble();
				} else if (isAssignableFrom(cls, FLOAT_CLASSES)) {
					return (float)value.getNumeric().asDouble();
				} else if (isAssignableFrom(cls, BigDecimal.class)) {
					return BigDecimal.valueOf(value.getNumeric().asDouble());
				} else if (isAssignableFrom(cls, BigInteger.class)) {
					return BigInteger.valueOf(value.getNumeric().asLong());
				}
				return null;
			case STRING:
				return value.getString();
			case ARRAY:
				if (cls.isArray()) {
					List<Value> valueList = value.asListArray();
					Class<?> compType = cls.getComponentType();
					@SuppressWarnings("unchecked")
					Object[] arr = (Object[]) Array.newInstance(compType, valueList.size());
					int i = 0;
					for (Value v : valueList) {
						arr[i++] = convertValue(compType, v);
					}
					return arr;
				} else if (isAssignableFrom(cls, List.class)) {
					List<Value> valueList = value.asListArray();
					List<Object> arrList = new ArrayList<>(valueList.size());
					valueList.forEach(v -> arrList.add(convertValue(Object.class, v)));
					return arrList;
				} else if (isAssignableFrom(cls, List.class)) {
					Map<Value, Value> valueMap = value.asAssocArray();
					Map<Object, Object> arrMap = new HashMap<>();
					valueMap.forEach((Value k, Value v) -> arrMap.put(convertValue(Object.class, k), convertValue(Object.class, v)));
					return arrMap;
				}				
				return null;
			case BOOLEAN:
				return value.asBoolean();
			case NULL:
				return null;
			default:
				return null;
		}
	}

	private static boolean isAssignableFrom(Class<?> cls, Class<?>... classes) {
// TODO: validate
		for (int i = 0; i < classes.length; i++) {
			if (classes[i].isAssignableFrom(cls)) {
				return true;
			}
		}
		return false;
	}

	public Value callMethod(String functionName, List<Value> values) {
		// try function name
		
		// try property name
		
		// convert returned value, 
		// convert Void to MYSTEROIUS
		// only primitive values (Long,  Double, String, null) are converted, 
		// other objects (BigDecimal, List, array, Map, etc) are wrapped into NativeObject (also general objects) 
		
		return null;
	}
	
	public Value unwrap() {
		// with cast method, NativeObjects can be converted to Rockstar representations (BigDecimal, List, array, Map) 
		return null;
	}

}
