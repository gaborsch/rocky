package rockstar.runtime;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import rockstar.statement.FunctionBlock;

public class RockstarFunctionWrapper {
	
	public static Object getProxyObject(Class<?> cls, FunctionBlock functionBlock, BlockContext ctx) {
		JavaInvocationHandler handler = new JavaInvocationHandler(cls, functionBlock, ctx);
		return Proxy.newProxyInstance(RockstarFunctionWrapper.class.getClassLoader(), 
				new Class<?>[] {cls}, 
				handler);
	}
	
	public static class JavaInvocationHandler implements InvocationHandler{
		
		private FunctionBlock functionBlock;
		private Method functionalMethod;
		private Class<?>[] paramClassList;
		private List<Class<?>> returnTypes;
		private BlockContext callCtx;

		public JavaInvocationHandler(Class<?> cls, FunctionBlock functionBlock, BlockContext outerCtx) {
			this.functionBlock = functionBlock;
			this.functionalMethod = selectFunctionalMethod(cls, functionBlock);
			this.paramClassList = extractParameterClasses(functionalMethod.getParameters());
			this.returnTypes = extractTypes(functionalMethod.getGenericReturnType());
			this.callCtx = new BlockContext(outerCtx, functionBlock.getName(), true);
		}

		private Class<?>[] extractParameterClasses(Parameter[] parameters) {			
			return Arrays.asList(parameters)
				.stream()
				.map(Parameter::getType)
				.collect(Collectors.toList())
				.toArray(new Class<?>[parameters.length]);
		}

		private static List<Class<?>> extractTypes(Type type ) {
			if (type instanceof Class) {
				Class<?> t = (Class<?>) type;
				if(t.isArray()) {
					List<Class<?>> typeList = new ArrayList<>();
					typeList.add(Arrays.class);
					typeList.addAll(extractTypes(t.getComponentType()));
					return typeList;
				} else {
					return List.of((Class<?>) t);
				}
			} else if (type instanceof GenericArrayType) {
				GenericArrayType t = (GenericArrayType) type;
				List<Class<?>> typeList = new ArrayList<>();
				typeList.add(Arrays.class);
				typeList.addAll(extractTypes(t.getGenericComponentType()));
				return typeList;
			} else if (type instanceof ParameterizedType) {
				ParameterizedType t = (ParameterizedType) type;
				List<Class<?>> typeList = new ArrayList<>();
				typeList.addAll(extractTypes(t.getRawType()));
				Type[] args = t.getActualTypeArguments();
				for (int i = 0; i < args.length; i++) {
					typeList.addAll(extractTypes(args[i]));
				}
				return typeList;
			} else if (type instanceof TypeVariable) {
				TypeVariable<?> t = (TypeVariable<?>) type;
				Type t0 = Object.class;
				if (t.getBounds().length >= 1) {
					t0 = t.getBounds()[0];
				}
				return extractTypes(t0);
			} else if (type instanceof WildcardType) {
				WildcardType t = (WildcardType) type;
				Type t0 = Object.class;
				if (t.getUpperBounds().length >= 1) {
					t0 = t.getUpperBounds()[0];
				}
				return extractTypes(t0);
			} else {
				throw new RockstarRuntimeException("Cannot map type " +type);
			}
		}

		private static Method selectFunctionalMethod(Class<?> cls, FunctionBlock functionBlock) {
			int blockParamCount = functionBlock.getParameterRefs().size();
			Method[] methods = cls.getDeclaredMethods();
			for (int i = 0; i < methods.length; i++) {
				Method m = methods[i];
				if (! m.isDefault() && m.getParameterCount() == blockParamCount) {
					return m;
				}			
			}
			throw new RockstarRuntimeException("Cannot proxy "+functionBlock.getName()+" for "+cls.getCanonicalName()+": no functional method found with " + blockParamCount + " parameters");
		}


		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			if (method != functionalMethod) {
				return method.invoke(proxy, args);
			}
			// wrap java parameters
			List<Value> functionParams = new ArrayList<>();
			for (int i = 0; i < args.length; i++) {
				functionParams.add(NativeObject.unwrapObject(args[i], paramClassList[i]));
			}
			// call functionBlock
			Value returnValue = functionBlock.call(callCtx, functionParams);
			// unwrap and cast return value
			NativeObject nativeValue = NativeObject.convertValueWithTypes(returnValue, returnTypes);
			// return the result
			return nativeValue.getNativeObject();
		}
	}
	
	
//	public static void main(String[] args) {
//		Method[] methods = JavaInvocationHandler.class.getDeclaredMethods();
//		int i = 1;
//		
////		Map<List<?>, Map<String, ? extends Number>> m = new HashMap<List<?>, Map<String, ? extends Number>>();
//		
//		Type t = methods[i].getGenericReturnType();
////		Type t = m.getClass(); 
//				
//		List<Class<?>> l = JavaInvocationHandler.extractTypes(t);
//		
//		l.forEach(c -> {System.out.println("type "+c.getCanonicalName());});
//		
////		System.out.println(methods[i]);
////		System.out.println(methods[i].getGenericReturnType());
////		ParameterizedType t = (ParameterizedType) methods[i].getGenericReturnType();
////		System.out.println(t.getActualTypeArguments().length);
////		System.out.println(t.getActualTypeArguments()[0]);
////		System.out.println(t.getActualTypeArguments()[0]);
//	}

}
