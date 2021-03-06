/*
 * Copyright (C) Chris Liao
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jmin.bee.impl.util;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.LinkedList;

import org.jmin.bee.impl.ProxyConnection;
import org.jmin.bee.impl.ProxyConnectionFactory;
import org.jmin.bee.impl.ProxyCsStatement;
import org.jmin.bee.impl.ProxyPsStatement;
import org.jmin.bee.impl.ProxyResultSet;
import org.jmin.bee.impl.ProxyStatement;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.LoaderClassPath;
import javassist.Modifier;

/**
 * 代理类生成工厂
 *
 * @author Chris
 * @version 1.0
 */
public final class ProxyClassUtil {
	
	/**
	 * main method
	 */
	public static void main(String[] arsg) throws Exception {
		String folder = "bin";
		if (arsg != null && arsg.length > 0)
			folder = arsg[0];
		writeProxyFile(folder);
	}

	/**
	 * write to disk folder
	 */
	public static void writeProxyFile(String folder) throws Exception {
		ProxyClassUtil builder = new ProxyClassUtil();
		CtClass[] ctClasses = builder.createJdbcProxyClasses();
		for (int i = 0; i < ctClasses.length; i++) {
			ctClasses[i].writeFile(folder);
		}
	}

  /**
   * create all proxy classes
   */
	public CtClass[] createJdbcProxyClasses()throws SQLException{	
		try{
			ClassPool classPool = ClassPool.getDefault();
			classPool.importPackage("java.sql");
			classPool.importPackage("org.jmin.bee.impl");
			classPool.appendClassPath(new LoaderClassPath(this.getClass().getClassLoader()));
			
			//。。。。。。。。Connection Begin。。。。。。。。。。。
			CtClass ctConIntf = classPool.get(Connection.class.getName());
			CtClass ctConSuperclass = classPool.get(ProxyConnection.class.getName());
			String ctConIntfProxyClassName ="org.jmin.bee.impl.ProxyConnectionImpl";
			CtClass ctConIntfProxyImplClass = classPool.makeClass(ctConIntfProxyClassName,ctConSuperclass);
			ctConIntfProxyImplClass.setInterfaces(new CtClass[]{ctConIntf});
			ctConIntfProxyImplClass.setModifiers(Modifier.FINAL);
			ctConIntfProxyImplClass.setModifiers(Modifier.PUBLIC);
			
			CtClass[] conConstructorParameters = new CtClass[]{
					classPool.get("org.jmin.bee.impl.PooledConnection"),
					classPool.get("org.jmin.bee.impl.FastConnectionPool")};	
		
			CtConstructor subClassConstructor = new CtConstructor(conConstructorParameters,ctConIntfProxyImplClass);
			subClassConstructor.setModifiers(Modifier.PUBLIC);
			StringBuffer body = new StringBuffer();
			body.append("{");
			body.append("super($$);");
			body.append("}");
			subClassConstructor.setBody(body.toString());
			ctConIntfProxyImplClass.addConstructor(subClassConstructor);
			//。。。。。。。。Connection End。。。。。。。。。。。
		
			//。。。。。。。。Statement Begin。。。。。。。。。。。
			CtClass ctStatementIntf = classPool.get(Statement.class.getName());
			CtClass ctStatementSuperclass= classPool.get(ProxyStatement.class.getName());
			String ctStatementIntfProxyClassName ="org.jmin.bee.impl.ProxyStatementImpl";
			CtClass ctStatementProxyImplClass = classPool.makeClass(ctStatementIntfProxyClassName,ctStatementSuperclass);
			ctStatementProxyImplClass.setInterfaces(new CtClass[]{ctStatementIntf});
			ctStatementProxyImplClass.setModifiers(Modifier.FINAL);
			ctStatementProxyImplClass.setModifiers(Modifier.PUBLIC);
			CtClass[] parameters = new CtClass[] {
					classPool.get("java.sql.Statement"),
					classPool.get("org.jmin.bee.impl.ProxyConnection") };
			subClassConstructor = new CtConstructor(parameters,ctStatementProxyImplClass);
			subClassConstructor.setModifiers(Modifier.PUBLIC);
			body.delete(0, body.length());
			body.append("{");
			body.append("super($$);");
			body.append("}");
			subClassConstructor.setBody(body.toString());
			ctStatementProxyImplClass.addConstructor(subClassConstructor);
		  //。。。。。。。。Statement Begin。。。。。。。。。。。
			
			//。。。。。。。。PreparedStatement Begin。。。。。。。。。。。
			CtClass ctPsStatementIntf = classPool.get(PreparedStatement.class.getName());
			CtClass ctPsStatementSuperclass= classPool.get(ProxyPsStatement.class.getName());
			String ctPsStatementIntfProxyClassName ="org.jmin.bee.impl.ProxyPsStatementImpl";
			CtClass ctPsStatementProxyImplClass = classPool.makeClass(ctPsStatementIntfProxyClassName,ctPsStatementSuperclass);
			ctPsStatementProxyImplClass.setInterfaces(new CtClass[]{ctPsStatementIntf});
			ctPsStatementProxyImplClass.setModifiers(Modifier.FINAL);
			ctPsStatementProxyImplClass.setModifiers(Modifier.PUBLIC);
			
			 parameters = new CtClass[] {
					classPool.get("java.sql.PreparedStatement"),
					classPool.get("org.jmin.bee.impl.ProxyConnection") };
			subClassConstructor = new CtConstructor(parameters,ctPsStatementProxyImplClass);
			subClassConstructor.setModifiers(Modifier.PUBLIC);
			body.delete(0, body.length());
			body.append("{");
			body.append("super($$);");
			body.append("}");
			subClassConstructor.setBody(body.toString());
			ctPsStatementProxyImplClass.addConstructor(subClassConstructor);
			//。。。。。。。。PreparedStatement End。。。。。。。。。。。
			
			//。。。。。。。。CallableStatement Begin。。。。。。。。。。。
			CtClass ctCsStatementIntf = classPool.get(CallableStatement.class.getName());
			CtClass ctCsStatementSuperclass= classPool.get(ProxyCsStatement.class.getName());
			String ctCsStatementIntfProxyClassName ="org.jmin.bee.impl.ProxyCsStatementImpl";
			CtClass ctCsStatementProxyImplClass = classPool.makeClass(ctCsStatementIntfProxyClassName,ctCsStatementSuperclass);
			ctCsStatementProxyImplClass.setInterfaces(new CtClass[]{ctCsStatementIntf});
			ctCsStatementProxyImplClass.setModifiers(Modifier.FINAL);
			ctCsStatementProxyImplClass.setModifiers(Modifier.PUBLIC);
			
			parameters = new CtClass[] {
					classPool.get("java.sql.CallableStatement"),
					classPool.get("org.jmin.bee.impl.ProxyConnection") };
			subClassConstructor = new CtConstructor(parameters,ctCsStatementProxyImplClass);
			subClassConstructor.setModifiers(Modifier.PUBLIC);
			
			body.delete(0, body.length());
			body.append("{");
			body.append("super($$);");
			body.append("}");
			subClassConstructor.setBody(body.toString());
			ctCsStatementProxyImplClass.addConstructor(subClassConstructor);
			//。。。。。。。。CallableStatement End。。。。。。。。。。。
			
			//。。。。。。。。 Result Proxy Begin。。。。。。。。。。。
			CtClass ctResultSetIntf = classPool.get(ResultSet.class.getName());
			CtClass ctResultSetSuperclass= classPool.get(ProxyResultSet.class.getName());
			String ctResultSetIntfProxyClassName ="org.jmin.bee.impl.ProxyResultSetImpl";
			CtClass ctResultSetIntfProxyImplClass = classPool.makeClass(ctResultSetIntfProxyClassName,ctResultSetSuperclass);
			ctResultSetIntfProxyImplClass.setInterfaces(new CtClass[]{ctResultSetIntf});
			ctResultSetIntfProxyImplClass.setModifiers(Modifier.FINAL);
			ctResultSetIntfProxyImplClass.setModifiers(Modifier.PUBLIC);
			
			parameters = new CtClass[]{
					classPool.get("java.sql.ResultSet"),
					classPool.get("org.jmin.bee.impl.ProxyStatementWrapper")};
			subClassConstructor = new CtConstructor(parameters,ctResultSetIntfProxyImplClass);
			subClassConstructor.setModifiers(Modifier.PUBLIC);
			body.delete(0, body.length());
			body.append("{");
			body.append("super($$);");
			body.append("}");
			subClassConstructor.setBody(body.toString());
			ctResultSetIntfProxyImplClass.addConstructor(subClassConstructor);
			//。。。。。。。。CallableStatement Result End。。。。。。。。。。。
			
			this.createProxyConnectionClass(classPool,ctConIntfProxyImplClass,ctConIntf,ctConSuperclass);
		  this.createProxyStatementClass(classPool,ctStatementProxyImplClass,ctStatementIntf,ctStatementSuperclass);
		  this.createProxyPsStatementClass(classPool,ctPsStatementProxyImplClass,ctPsStatementIntf,ctPsStatementSuperclass);
		  this.createProxyCsStatementClass(classPool,ctCsStatementProxyImplClass,ctCsStatementIntf,ctCsStatementSuperclass);
		  this.createProxyResultSetClass(classPool,ctResultSetIntfProxyImplClass,ctResultSetIntf,ctResultSetSuperclass);
	
		  CtClass ctProxyConnectionFactoryClass = classPool.get(ProxyConnectionFactory.class.getName());
			CtMethod newCtMethodm=ctProxyConnectionFactoryClass.getDeclaredMethod("createProxyConnection", conConstructorParameters);
			body.delete(0, body.length());
			body.append("{");
			body.append("return new ProxyConnectionImpl($$);");
			body.append("}");
			newCtMethodm.setBody(body.toString());
			
			return new CtClass[]{
					ctConIntfProxyImplClass,
					ctStatementProxyImplClass,
					ctPsStatementProxyImplClass,
					ctCsStatementProxyImplClass,
					ctResultSetIntfProxyImplClass,
					ctProxyConnectionFactoryClass};
		}catch(Throwable e){
			throw new SQLException(e);
		}
	}

	/**
	 * build ProxyConnectionImpl
	 */
	private Class createProxyConnectionClass(ClassPool classPool,CtClass ctConIntfProxyClass,CtClass ctConIntf,CtClass ctConSuperClass)throws Exception{
		CtMethod[] ctSuperClassMethods = ctConSuperClass.getDeclaredMethods();
		HashSet notNeedAddProxyMethods= new HashSet();
		for(int i=0,l=ctSuperClassMethods.length;i<l;i++){
			int modifiers=ctSuperClassMethods[i].getModifiers();
			if((!Modifier.isAbstract(modifiers) && (Modifier.isPublic(modifiers)||Modifier.isProtected(modifiers)))
					|| Modifier.isFinal(modifiers)|| Modifier.isStatic(modifiers)|| Modifier.isNative(modifiers)){
				notNeedAddProxyMethods.add(ctSuperClassMethods[i].getName() + ctSuperClassMethods[i].getSignature());
			}
		}
		
		LinkedList<CtMethod> linkedList = new LinkedList();
		resolveInterfaceMethods(ctConIntf,linkedList,notNeedAddProxyMethods);
		
		StringBuffer methodBuffer = new StringBuffer();
		for(CtMethod ctMethod:linkedList){
			String methodName = ctMethod.getName();
			CtMethod newCtMethodm = CtNewMethod.copy(ctMethod, ctConIntfProxyClass, null);
			newCtMethodm.setModifiers(Modifier.PUBLIC);
			
			methodBuffer.delete(0, methodBuffer.length());
			methodBuffer.append("{");
			methodBuffer.append("this.updateLastActivityTime();");
			
			if(methodName.equals("createStatement")){
				methodBuffer.append("  return new ProxyStatementImpl(this.delegate.createStatement($$),this);");	
			}else if(methodName.equals("prepareStatement")){
//				methodBuffer.append("	StatementCacheKey key = new StatementCacheKey($$,StatementCacheKey.PS_STATEMENT);");
//				methodBuffer.append(" PreparedStatement statement=this.pooledConnection.getStatement(key);");
//				methodBuffer.append(" if(statement==null){");
//				methodBuffer.append("    statement=this.delegate.prepareStatement($$);");
//				methodBuffer.append("    this.pooledConnection.putStatement(key,statement);");
//				methodBuffer.append(" }");
//				methodBuffer.append("  return new ProxyPsStatementImpl(statement,this);");					
  		  methodBuffer.append("  return new ProxyPsStatementImpl(this.delegate.prepareStatement($$),this);");	
			}else if(methodName.equals("prepareCall")){
//				methodBuffer.append(" CallableStatement statement=(CallableStatement)this.pooledConnection.getStatement($1);");
//				methodBuffer.append(" if(statement==null){");
//				methodBuffer.append("   statement=this.delegate.prepareCall($$);");
//				methodBuffer.append("   this.pooledConnection.putStatement($1,statement);");
//				methodBuffer.append(" }");
//			  methodBuffer.append(" return new ProxyCsStatementImpl(statement,this);");
 			 methodBuffer.append(" return new ProxyCsStatementImpl(this.delegate.prepareCall($$),this);");
			}else{
				if (newCtMethodm.getReturnType() == CtClass.voidType)
					methodBuffer.append(" this.delegate." + methodName + "($$);");
				else
					methodBuffer.append(" return this.delegate." + methodName + "($$);");
		  }
			methodBuffer.append("}");
		
			newCtMethodm.setBody(methodBuffer.toString());
			ctConIntfProxyClass.addMethod(newCtMethodm);
			 
		}
		return ctConIntfProxyClass.toClass();
	}
	
	/**
	 * build ProxyStatementImpl
	 */
	private Class createProxyStatementClass(ClassPool classPool, CtClass ctStatementProxyClass,CtClass ctStatementIntf, CtClass ctStatementSuperClass) throws Exception {
		CtMethod[] ctSuperClassMethods = ctStatementSuperClass.getDeclaredMethods();
		HashSet superClassSignatureSet = new HashSet();
		for (int i = 0, l = ctSuperClassMethods.length; i < l; i++) {
			int modifiers = ctSuperClassMethods[i].getModifiers();
			if ((!Modifier.isAbstract(modifiers) && (Modifier.isPublic(modifiers) || Modifier.isProtected(modifiers)))
					|| Modifier.isFinal(modifiers) || Modifier.isStatic(modifiers) || Modifier.isNative(modifiers)) {
				superClassSignatureSet.add(ctSuperClassMethods[i].getName() + ctSuperClassMethods[i].getSignature());
			}
		}

		LinkedList<CtMethod> linkedList = new LinkedList();
		resolveInterfaceMethods(ctStatementIntf, linkedList, superClassSignatureSet);

		StringBuffer methodBuffer = new StringBuffer();
		for (CtMethod ctMethod : linkedList) {
			String methodName = ctMethod.getName();
			CtMethod newCtMethodm = CtNewMethod.copy(ctMethod, ctStatementProxyClass, null);
			newCtMethodm.setModifiers(Modifier.PUBLIC);

			methodBuffer.delete(0, methodBuffer.length());
			methodBuffer.append("{");
			methodBuffer.append("this.updateLastActivityTime();");

			if (methodName.equals("executeQuery")) {
				methodBuffer.append(" return new ProxyResultSetImpl(this.delegate.executeQuery($$),this);");
			} else {
				if (newCtMethodm.getReturnType() == CtClass.voidType)
					methodBuffer.append(" this.delegate." + methodName + "($$);");
				else
					methodBuffer.append(" return this.delegate." + methodName + "($$);");
			}
			methodBuffer.append("}");

			newCtMethodm.setBody(methodBuffer.toString());
			ctStatementProxyClass.addMethod(newCtMethodm);

		}
		return ctStatementProxyClass.toClass();
	}
	
	/**
	 * build ProxyPsStatementImpl
	 */
	private Class createProxyPsStatementClass(ClassPool classPool,CtClass ctPsStatementProxyClass,CtClass ctPsStatementIntf,CtClass ctPsStatementSuperClass)throws Exception{
		CtMethod[] ctSuperClassMethods = ctPsStatementSuperClass.getDeclaredMethods();
		HashSet superClassSignatureSet= new HashSet();
		for(int i=0,l=ctSuperClassMethods.length;i<l;i++){
			int modifiers=ctSuperClassMethods[i].getModifiers();
			if((!Modifier.isAbstract(modifiers) && (Modifier.isPublic(modifiers)||Modifier.isProtected(modifiers)))
					|| Modifier.isFinal(modifiers)|| Modifier.isStatic(modifiers)|| Modifier.isNative(modifiers)){
				superClassSignatureSet.add(ctSuperClassMethods[i].getName() + ctSuperClassMethods[i].getSignature());
			}
		}
	
		LinkedList<CtMethod> linkedList = new LinkedList();
		resolveInterfaceMethods(ctPsStatementIntf,linkedList,superClassSignatureSet);
		
		StringBuffer methodBuffer = new StringBuffer();
		for(CtMethod ctMethod:linkedList){
			String methodName = ctMethod.getName();
			CtMethod newCtMethodm = CtNewMethod.copy(ctMethod, ctPsStatementProxyClass, null);
			newCtMethodm.setModifiers(Modifier.PUBLIC);
			
			methodBuffer.delete(0, methodBuffer.length());
			methodBuffer.append("{");
			methodBuffer.append("this.updateLastActivityTime();");
			
			if(methodName.equals("executeQuery")){
			  methodBuffer.append(" return new ProxyResultSetImpl(this.delegate.executeQuery($$),this);");		
			}else{
				if(newCtMethodm.getReturnType() == CtClass.voidType)
					methodBuffer.append(" this.delegate." + methodName + "($$);");
				else
					methodBuffer.append(" return this.delegate." + methodName + "($$);");
			}
			methodBuffer.append("}");
			
			newCtMethodm.setBody(methodBuffer.toString());
			ctPsStatementProxyClass.addMethod(newCtMethodm);
			 
		}
		return ctPsStatementProxyClass.toClass();
	}

	/**
	 * build ProxyCsStatementImpl
	 */
	private Class createProxyCsStatementClass(ClassPool classPool,CtClass ctCsStatementProxyClass,CtClass ctCsStatementIntf,CtClass ctCsStatementSuperClass)throws Exception{
		CtMethod[] ctSuperClassMethods = ctCsStatementSuperClass.getDeclaredMethods();
		HashSet superClassSignatureSet= new HashSet();
		for(int i=0,l=ctSuperClassMethods.length;i<l;i++){
			int modifiers=ctSuperClassMethods[i].getModifiers();
			if((!Modifier.isAbstract(modifiers) && (Modifier.isPublic(modifiers)||Modifier.isProtected(modifiers)))
					|| Modifier.isFinal(modifiers)|| Modifier.isStatic(modifiers)|| Modifier.isNative(modifiers)){
				superClassSignatureSet.add(ctSuperClassMethods[i].getName() + ctSuperClassMethods[i].getSignature());
			}
		}
		
		LinkedList<CtMethod> linkedList = new LinkedList();
		resolveInterfaceMethods(ctCsStatementIntf,linkedList,superClassSignatureSet);
		
		StringBuffer methodBuffer = new StringBuffer();
		for(CtMethod ctMethod:linkedList){
			String methodName = ctMethod.getName();
			CtMethod newCtMethodm = CtNewMethod.copy(ctMethod, ctCsStatementProxyClass, null);
			newCtMethodm.setModifiers(Modifier.PUBLIC);
			
			methodBuffer.delete(0, methodBuffer.length());
			methodBuffer.append("{");
			methodBuffer.append("this.updateLastActivityTime();");
		 
			if(methodName.equals("getResultSet")){
				methodBuffer.append(" return new ProxyResultSetImpl(this.delegate.getResultSet($$),this);");		
			}else{
				if(newCtMethodm.getReturnType() == CtClass.voidType)
					methodBuffer.append(" this.delegate." + methodName + "($$);");
				else
					methodBuffer.append(" return this.delegate." + methodName + "($$);");
			}
			methodBuffer.append("}");
			
			newCtMethodm.setBody(methodBuffer.toString());
			ctCsStatementProxyClass.addMethod(newCtMethodm);
			 
		}
		return ctCsStatementProxyClass.toClass();
	}
	
	/**
	 * build ProxyResultSetImpl
	 */
	private Class createProxyResultSetClass(ClassPool classPool,CtClass ctResultSetIntfProxyClass,CtClass ctResultSetIntf,CtClass ctResultSetIntfSuperClass)throws Exception{
		CtMethod[] ctSuperClassMethods = ctResultSetIntfSuperClass.getDeclaredMethods();
		HashSet superClassSignatureSet= new HashSet();
		for(int i=0,l=ctSuperClassMethods.length;i<l;i++){
			int modifiers=ctSuperClassMethods[i].getModifiers();
			if((!Modifier.isAbstract(modifiers) && (Modifier.isPublic(modifiers)||Modifier.isProtected(modifiers)))
					|| Modifier.isFinal(modifiers)|| Modifier.isStatic(modifiers)|| Modifier.isNative(modifiers)){
				superClassSignatureSet.add(ctSuperClassMethods[i].getName() + ctSuperClassMethods[i].getSignature());
			}
		}
		
		LinkedList<CtMethod> linkedList = new LinkedList();
		resolveInterfaceMethods(ctResultSetIntf,linkedList,superClassSignatureSet);
		StringBuffer methodBuffer = new StringBuffer();
		
		for(CtMethod ctMethod:linkedList){
			String methodName = ctMethod.getName();
			CtMethod newCtMethodm = CtNewMethod.copy(ctMethod, ctResultSetIntfProxyClass, null);
			newCtMethodm.setModifiers(Modifier.PUBLIC);
			
			methodBuffer.delete(0, methodBuffer.length());
			methodBuffer.append("{");
			methodBuffer.append("this.updateLastActivityTime();");
			
			if (ctMethod.getReturnType() == CtClass.voidType)
				methodBuffer.append("this.delegate." + methodName + "($$);");
			else
				methodBuffer.append("return this.delegate." + methodName + "($$);");
			 
			methodBuffer.append("}");
			newCtMethodm.setBody(methodBuffer.toString());
			ctResultSetIntfProxyClass.addMethod(newCtMethodm);
		}		   
		return ctResultSetIntfProxyClass.toClass();
	}
	
	
	/**
	 * find out all methods
	 */
	private void resolveInterfaceMethods(CtClass interfaceClass,LinkedList linkedList,HashSet exitSignatureSet)throws Exception{
		CtMethod[] ctMethods = interfaceClass.getDeclaredMethods();
		for(int i=0;i<ctMethods.length;i++){
			int modifiers=ctMethods[i].getModifiers();
			String signature = ctMethods[i].getName()+ctMethods[i].getSignature();
			if(Modifier.isAbstract(modifiers) 
					&& (Modifier.isPublic(modifiers)||Modifier.isProtected(modifiers))
					&& !Modifier.isStatic(modifiers) 
					&& !Modifier.isFinal(modifiers) 
					&& !exitSignatureSet.contains(signature)){
				
				linkedList.add(ctMethods[i]);
				exitSignatureSet.add(signature);
			}
		}
		
		CtClass[] superInterfaces=interfaceClass.getInterfaces();
		for(int i=0;i<superInterfaces.length;i++){
			resolveInterfaceMethods(superInterfaces[i],linkedList,exitSignatureSet);
		}
	}
}
