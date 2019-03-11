### 开发框架

#### 创建框架项目

- 创建项目
- 添加依赖

#### 创建示例项目

- 依赖于框架项目

#### 定义框架配置顶

- 在示例项目中配置properties文件

#### 加载配置项

- 创建ConfigHelper助手类读取properties配置文件
  - ConfigConstant常量类
  - PropsUtil工具类获取配置文件实现ConfigHelper

#### 开发一个类加载器

- 类加载器加载该基础包名下的所有类

  - ClassUtil工具类提供与类操作相关的方法

    - 获取类加载器 **ClassLoader getClassLoader()**

      截取当前线程中的ClassLoader即可

    - 加载类需要提供类名与是否初始化的标志 **Class<?> loadClass(String classname,boolean** isInitialized)

    - 获取指定包名下的所有类,需要根据包名并将其中转换为文件路径,读取class文件或jar包,获取指定的类名去加载类 **Set<Class<?>> getClassSet(String packageName)**

      - **void doAddClass(Set<Class<?>> classSet,String name)**

      - **void addClass(Set<Class<?>> classSet,String packagePath,String packageName)**

- 目标是在控制器类上使用Controller注解,在控制器类的方法上使用Action注解,在服务类上使用Service注解,在控制器类中可使用Inject注解将服务器类依赖注入进来
  - 自定义4个注解类
    - **public @interface Controller**
    - **public @interface Action**
    - **public @interface Service**
    - **public @interface Inject**

- 在配置文件中制定了base_package是整个应用的基础包名,通过ClassUtil加载的类都需要基于该基础包名.所以有必要提供一个ClassHelper助手类,让它分别获取应用包名的所有类,应用包名下所有Service类即Controller类.另外Controller注解与Service注解的类所产生的对象,理解为框架所管理的Bean.所以有必要在ClassHelper类中增加一个获取应用包名下所有Bean类方法
  - 类操作助手类 **ClassHelper**(封装了ClassUtil)
    - 定义类集合(用于存放所加载的类) **Set<Class<?>> CLASS_SET**
    - 获取应用包下的所有类 **Set<Class<?>> getClassSet()** 即CLASS_SET
    - 获取应用包下所有Service类 **Set<Class<?>> getServiceClassSet()**
    - 获取应用包名下所有Controller类  **Set<Class<?>> getControllerClassSet()**
    - 获取应用包下所有Bean类(包括:service,controller等) **Set<Class<?>> getBeanClassSet()**

#### 实现Bean容器

- 使用ClassHelper类可以获取所加载的类,但无法通过类来实例化对象.因此需要提供反射工具类,让它封装Java反射相关的API,对外提供更好用的工具方法

  **ReflectionUtil**

  - 创建实例 **Object newInstance(Class<?> cls)**
  - 调用方法 **Object invokeMethod(Object obj,Method method,Object … args)**
  - 设置成员变量的值 **void setField(Object obj,Field field,Object value)**

- 需要获取所有被框架管理的Bean类,此时需要ClassHelper类的**getBeanClassSet方法**,然后循环调用ReflectionUtil类的**newInstance方法**,根据类来实例化对象,最后将每次创建的对象存放在一个静态的**Map<Class<?>,Object>**,我们需要随时获取该Map,通过Key(类名)去获取所对应的value(Bean对象)

  **BeanHelper**

  - 定义Bean映射(用于存放Bean类与Bean实例的映射关系) **final Map<Class<?>,Object> BEAN_MAP**
  - **static加载**中实例化对象并存放于BEAN_MAP中
  - 获取Bean映射 **Map<Class<?>,Object> getBeanMap()** 即BEAN_MAP
  - 获取Bean实例 **<T> T  getBean(Class<T> cls)**

#### 实现依赖注入功能

- 在Controller中定义Service成员变量,然后在Controller的Action方法中调用Service成员变量的方法,所以现在需要实现实例化Service成员变量

  故使用**Inject注解**来实现**Service实例化**

  通过**IOC控制反转**(或称为DI(Dependency Injection,依赖注入),先通过BeanHelper获取所有的**Bean Map(Map<Class<?>,Object>结构,记录了类与对象的映射关系)**.然后遍历这个映射关系,分别取出Bean类与其实例,进而通过**反射获取**类中的所有成员变量.继续**遍历**这些成员变量,在循环中判断当前成员变量是否**带有Inject注解**,若有该注解则提出Bean类中的**实例**,最后通过**ReflectionUtil#setField**方法**修改当前成员变量的值**

  **lochelper**

  ```java
      static {
          //获取所有的Bean类与Bean实例之间的映射关系(Bean Map)
          Map<Class<?>,Object> beanMap=BeanHelper.getBeanMap();
          if(CollectionUtil.isNotEmpty(beanMap)){
              //遍历Bean Map
              for(Map.Entry<Class<?>,Object>beanEntry:beanMap.entrySet()){
                  //从BeanMap中获取Bean类与Bean实例
                  Class<?> beanClass=beanEntry.getKey();
                  Object beanInstance=beanEntry.getValue();
                  //截取Bean类定义的所有成员变量(Bean Field)
                  Field[] beanFields=beanClass.getDeclaredFields();
                  if(ArrayUtil.isNotEmpty(beanFields)){
                      //遍历Bean Field
                      for(Field beanField:beanFields){
                          //判断当前Bean Field是否带有Inject注解
                          if(beanField.isAnnotationPresent(Inject.class)){
                              //在Bean Map获取Bean Field对应的实例
                              Class<?> beanFieldClass=beanField.getType();
                              Object beanFieldInstance=beanMap.get(beanFieldClass);
                              if(beanFieldInstance!=null){
                                  //通过反射初始化BeanField的值
                                  ReflectionUtil.setField(beanInstance,beanField,beanFieldInstance);
                              }
                          }
                      }
                  }
              }
          }
  
      }
  ```

  **locHelper**的静态块实现相关逻辑就完成了IOC容器的**初始化工作**,并且在locHelper这个类被加载的时候就会加载它的静态块.后面我们则需要找一个**统一的地方来加载这个locHelpe**r

- 此时在IOC框架中所**管理的对象都是单例的**,由于底层还是从BeanHelper中获取Bean Map的,而Bean Map中的对象都是事先创建好并放入这个Bean容器的所有的对象都是单例的

#### 加载Controller

- 通过ClassHelper,我们可以获取所有定义了**Controller注解的类**,可以通过**反射获取**该类中所有带有**Action注解的方法**,获取Action注解中的**请求表达式**,进而**获取请求方法与请求路径**,**封装**一个请求对象(Request)与处理对象(Handle),最后将**Request与Handler建立一个映射关系**,放入一个**Action Map**中,并提供一个可根据**请求方法与请求路径获取处理对象**的方法

  - 首先,定义一个**Request类**
    - 定义**String requestMethod  requestPath**
    - 重写**hashCode()  equals()**

  - 然后,编写一个名为**Handler类**

    - 定义 **Class<?> controllerClass  Method actionMethod**

  - 最后,编写**ControllerHelper类**

    ```java
    public final class ControllerHelper {
    
        /**
         * 用于存放请求与处理器的映射关系(简称 Action Map)
         */
        private static final Map<Request, Handler> ACTION_MAP=new HashMap<>();
        
        static {
            //获取所有的Controller类
            Set<Class<?>> controllerClassSet=ClassHelper.getControllerClassSet();
            if(CollectionUtil.isNotEmpty(controllerClassSet)){
                //遍历这些Controller类
                for(Class<?> controllerClass :controllerClassSet){
                    //获取Controller类中定义的方法
                    Method[] methods=controllerClass.getDeclaredMethods();
                    if(ArrayUtil.isNotEmpty(methods)){
                        //遍历这些Controller类中的方法
                        for(Method method:methods){
                            //判断当前是否带有Action注释
                            if(method.isAnnotationPresent(Action.class)){
                                //从Action注解中获取URL映射规则
                                Action action=method.getAnnotation(Action.class);
                                String mapping=action.value();
                                //验证URL映射规则
                                if(mapping.matches("\\w+:/\\w*")){
                                    String[] array=mapping.split(":");
                                    if(ArrayUtil.isNotEmpty(array)&&array.length ==2){
                                        //获取请求方法与请求路径
                                        String requestMethod=array [0];
                                        String requestPath = array [1];
                                        Request request=new Request(requestMethod,requestPath);
                                        Handler handler=new Handler(controllerClass,method);
                                        //初始化Action Map
                                        ACTION_MAP.put(request,handler);
                                    }
                                    
                                }
                            }
                        }
                    }
                }
            }
        }
    
        /**
         * 获取Handler
         */
        public static Handler getHandler(String requestMethod,String requestPath){
            Request request=new Request(requestMethod,requestPath);
            return ACTION_MAP.get(request);
        }
    }
    ```

    在ControllerHelper中**封装了一个Action Map**,通过它来存放Request月Handler之间的**映射关系**,然后通过该类**获取所有带Controller注解的类**,接着**遍历**这些Controller类,从Action注解中**提取URL**,最后通过**初始化**Request与Handler之间的**映射关系**

#### 初始化框架

- 通过上面的过程创建了各个Helper类,这四个类需要通过一个入口程序来加载它们,实际上是加载它们的静态块 

  **HelperLoader**

  ```java
      public static void init(){
          Class<?>[] classList={
                  ClassHelper.class,
                  BeanHelper.class,
                  IocHelper.class,
                  Controller.class
          };
          for(Class<?> cls:classList){
              ClassUtil.loadClass(cls.getName(),true);
          }
      }
  ```

#### 请求转发器

- 以上过程都是为了这一步做准备,我们现在需要编写一个**Servlet**,让它来**处理所有的请求**.从HttpServletRequest对象中**获取请求方法与请求路径**,通过**ControllerHelper#getHandler** 方法来获取Handler对象

  当拿到**Handler对象**后我们可以方便地**获取Controller的类**,进而通过**BeanHelper.getBean**方法获取Controller的实例对象

  随后可以从HttpServletRequest对象中**获取所有请求参数**,并将其**初始化到一个名为Param的对象**中

  **Param类**

  ```java
      private Map<String, Object> paramsMap;
      
      public Param(Map<String,Object> pragmaMap){
          this.paramsMap =pragmaMap;
      }
  
      /***
       * 根据参数名获取long型参数值
       */
      public long getLong(String name){
          return CastUtil.castLong(paramsMap.get(name));
      }
  
      /**
       * 获取所有字段消息
       */
      public Map<String, Object> getMap(){
          return paramsMap;
      }
  ```

  在Param类中,会有一系列的**get方法**,可通过参数名获取指定类型的参数值,也可以获取所有参数的Map结构

  还可从**Handler对象中获取Action的方法返回值**,该返回值可能有两种情况:

  1.若返回值是**View类型**的视图对象,则返回一个**JSP**页面

  2.若返回值是**Data类型**的数据对象,则返回一个**JSON**数据

  根据上述情况来做不同的处理

  **View类**

  - 视图路径 private **String** **path**
  - 模型数据 private **Map<String,Object> model**
  - **View addModel(String key,Object obj)**

  **Data类**

  - 模型数据 private **Object model**

  返回的Data类型的数据封装了一个**Object类型的模型数据**,框架会将该对象写**入HttpSerlvetResponse对象**中,从而输出到浏览器

- 以下便是MVC框架中最核心的**DispatcherServlet类**

  - **StreamUtil** 流操作工具类
    - **从流中获取字符串** String getString(InputStream is)

  - **CodecUtil** 编码与解码操作工具类
    - **将URL编码和解码** String encodeURL(String source) and String decodeURL(String source)

  - **JsonUtil** JSON工具类
    - **处理JSON和POJO之间的转换** <T> String toJson(T obj) and <T> T fromJson(String json,Class<T> type)

  

  ```java
  @WebServlet(urlPatterns = "/*" , loadOnStartup = 0)
  public class DispatcherServlet extends HttpServlet {
  
  
      @Override
      public void init() throws ServletException {
         //初始化相关Helper类
          HelperLoader.init();
          //获取ServletContext对象(用于注释Servlet )
          ServletContext servletContext = getServletContext();
          //注册处理JSP的Servlet
          ServletRegistration jspServlet=servletContext.getServletRegistration("jsp");
          //注册处理静态资源的默认Servlet
          ServletRegistration defaultServlet=servletContext.getServletRegistration("default");
          defaultServlet.addMapping(ConfigHelper.getAppAssetPath()+"*");
      }
  
      @Override
      protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
          //获取请求方法与请求路径
          String requestMethod = req.getMethod().toLowerCase();
          String requestPath=req.getPathInfo();
          //获取Action处理器
          Handler handler = ControllerHelper.getHandler(requestMethod,requestPath);
          if(handler!=null){
              //获取Controller类及其Bean实例
              Class<?> controllerClass=handler.getControllerClass();
              Object controllerBean= BeanHelper.getBean(controllerClass);
              //创建请求参数对象
              Map<String,Object> paramMap=new HashMap<String,Object>();
              Enumeration<String> paramNames=req.getParameterNames();
              while(paramNames.hasMoreElements()){
                  String paramName=paramNames.nextElement();
                  String paramValue=req.getParameter(paramName);
                  paramMap.put(paramName,paramValue);
              }
              String body= CodecUtil.decodeURL(StreamUtil.getString(req.getInputStream()));
              if(StringUtil.isNotEmpty(body)){
                  String[] params=StringUtil.splitString(body,"&");
                  if(ArrayUtil.isNotEmpty(params)){
                      for(String param:params){
                          String[] array=StringUtil.splitString(param,"=");
                          if(ArrayUtil.isNotEmpty(array)&&array.length==2){
                              String paramName=array [0];
                              String paramValue=array [1];
                              paramMap.put(paramName,paramValue);
                          }
                      }
                  }
              }
              Param param = new Param(paramMap);
              //调用Action方法
              Method actionMethod=handler.getActionMethod();
              Object result= ReflectionUtil.invokeMethod(controllerBean,actionMethod,param);
              //处理Action方法返回值
              if(result instanceof View){
                  //返回JSP页面
                  View view = (View) result;
                  String path=view.getPath();
                  if(StringUtil.isNotEmpty(path)){
                      if(path.startsWith("/")){
                          resp.sendRedirect(req.getContextPath()+path);
                      }else{
                          Map<String,Object> model=view.getModel();
                          for(Map.Entry<String,Object> entry:model.entrySet()){
                              req.setAttribute(entry.getKey(), entry.getValue());
                          }
                          req.getRequestDispatcher(ConfigHelper.getAppJspPath()+path).forward(req,resp);
                      }
                  }else if(req instanceof Data){
                      //返回JSON数据
                      Data data=(Data) result;
                      Object model=data.getModel();
                      if(model!=null){
                          resp.setContentType("application/json");
                          resp.setCharacterEncoding("UTF-8");
                          PrintWriter writer=resp.getWriter();
                          String json=JsonUtil.toJson(model);
                          writer.write(json);
                          writer.flush();
                          writer.close();
                      }
                  }
              }
          }
      }
  
  
  }
  
  ```

  

- 通过这个DisPatcherServlet来处理所有的请求,根据请求消息从ControllerHelper中获取对应的Action方法,然后使用反射技术调用Action方法,同时需要具体的传入方法参数,最后拿到返回值并判断返回值的类型,进行相应的处理

### 开发AOP框架

#### 定义切面注解

- 在框架中添加**Aspect的注解**

  ```java
  @Target(ElementType.TYPE)//设置该注解只能应用于类上
  @Retention(RetentionPolicy.RUNTIME)
  public @interface Aspect {
      /**
       * 注解类
       */
      Class<? extends Annotation> value();
  }
  ```

#### 搭建代理框架

- 添加**Proxy**的接口
  - 执行链式代理 **Object doProxy(ProxyChain proxyChain) throws Throwable**

- 添加**ProxyChain类**
  - 定义了一系列成员变量包括 **targetClass(目标类),targetObject(目标对象),targetMethod(目标方法),methodProxy(方法代理),methodParams(方法参数)**此外还包括了**proxyList(代理列表),proxyIndex(代理索引)**.这些成员变量在构造器中进行初始化,并提供几个重要的获值方法.
  - **doProxyChain 方法**,在该方法中,我们通过**proxyIndex**来充当代理对象的计数器,若尚未达到ProxyList的上限,则从**proxyList中取出相应的Proxy对象**,并**调用doProxy方法**.在Proxy接口的实现中提供相应的**横切逻辑**,并调用**doProxyChain方法**,随后将**再次调用当前ProxyChain对象的doProxyChain方法**,直到proxyIndex达到proxyList的**上限为止**,最后调用**methodProxy的invokeSuper方法**,执行目标对象的逻辑;

- 写一个提供一个**创建代理对象的方法**,输入**一个目标类**和**一组Proxy接口实现**,输出一个代理对象,将它命名为**ProxyManager**,让它来**创建所有的代理对象**
  - **static <T> T createProxy(final Class<?> targetClass,final List<Proxy> proxyList)**   通过Enhance#create方法来创建代理对象,将intercept的参数传入ProxyChain的构造器即可

- 写一个抽象类,让它提供一个模板方法,并在该抽象类的具体实现中扩展相应的抽象方法

  - **AspectProxy类 implements Proxy**
    - **Object doProxy(ProxyChain proxyChain) throws Throwable**  从proxyChain参数中获取了目标类、目标方法与方法参数,随后通过try catch finally代码块来实现调用框架,从框架中抽象出一系列的"钩子方法"
    - **void begin() ; boolean intercept(Class<?> cls,Method method,Object[] params) throws Throwable**
    - **void before(Class<?> cls,Method method,Object[] params) throws Throwable**
    - **void after(Class<?> cls,Method method,Object[] params) throws Throwable**
    - **void error(Class<?> cls,Method method,Object[] params) throws Throwable; void end()**

  - 抽象方法可在AspectProxy的子类中有选择性地进行实现

#### 加载AOP框架

- 为了**加载AOP框架**,我们需要编写一个名为**AopHelper的类**,然后将其添加到**HelperLoader类**中,在AopHelper类中需要**获取所有的目标类及其被拦截的切面类实例**,并通过ProxyManager#createProxy方法来**创建代理对象**,最后将其放入**Bean Map**中
- 首先需要在BeanHelper类中添加一个**setBean**方法,用于将**Bean实例放入Bean Map**中
  - **static void setBean(Class<?> cls,Object obj)**

- 然后需要扩展AspectProxy抽象类的**所有具体类**,还需要获取带有**Aspect注解的所有类**

  - 需要在**ClassHelper**添加两个方法
    - **static Set<Class<?>> getClassSetBySuper(Class<?> superClass)**获取应用包名下某父类(或接口)的所有子类(或实现类)
    - **static Set<Class<?>> getClassSetByAnnotation(Class<? extends Annotation> annotationClass)**   获取应用包名下带有某注解的所有类
    - 由以上工具方法后可以在AopHelper类中编写一个带有Aspect注解的所有类

  - 在AopHelper中封装一个方法

    ```java
        private static Set<Class<?>> createTargetClassSet(Aspect aspect)throws Exception{
            Set<Class<?>> targetClassSet=new HashSet<>();
            Class<? extends Annotation> annotation=aspect.value();
            if(annotation!=null&&!annotation.equals(Aspect.class)){
                targetClassSet.addAll(ClassHelper.getClassSetByAnnotation(annotation));
            }
            return targetClassSet;
    ```

  获取Aspect注解中设置的注解类,若不是则调用ClassHelper#getClassSetByAnnotation方法获取相关类,并把这些类放入目标类集合,最终返回这个集合

- 紧接着我们需要获取代理类及其目标类集合之间的映射关系,一个代理类可对应一个或多个目标类,这里所有的代理类指的是切面类

  ```java
  private static Map<Class<?>,Set<Class<?>>> createProxyMap() throws Exception{
      Map<Class<?>,Set<Class<?>>> proxyMap=new HashMap<> ();
      Set<Class<?>> proxyClassSet=ClassHelper.getClassSetBySuper(AspectProxy.class);
      for (Class<?> proxyClass:proxyClassSet){
          if(proxyClass.isAnnotationPresent(Aspect.class)){
              Aspect aspect=proxyClass.getAnnotation(Aspect.class);
              Set<Class<?>> targetClassSet=createTargetClassSet(aspect);
              proxyMap.put(proxyClass,targetClassSet);
          }
      }
      return proxyMap;
  }
  ```

  代理类需要扩展AspectProxy抽象类,还需要带有Aspect注解,只有满足这两条件,才能根据Aspect注解中所定义的注解属性去获取该注解所对应的目标类集合,然后才能建立代理类与目标类集合之间的映射关系,最终返回这个映射关系

- 获取了映射关系就能根据这个关系分析出目标类与代理对象列表之间的映射关系

  ```java
      private static Map<Class<?>, List<Proxy>> createTargetMap(Map<Class<?>,Set<Class<?>>> proxyMap)throws Exception{
          Map<Class<?>, List<Proxy>>  targetMap=new HashMap<>();
          for(Map.Entry < Class < ? > , Set < Class < ? > > >  proxyEntry : proxyMap.entrySet()){
              Class<?> proxyClass =proxyEntry.getKey();
              Set<Class<?>> targetClassSet=proxyEntry.getValue();
              for(Class<?> targetClass:targetClassSet){
                  Proxy proxy=(Proxy) proxyClass.newInstance();
                  if(targetMap.containsKey(targetClass)){
                      targetMap.get(targetClass).add(proxy);
                  }else{
                      List<Proxy> proxyList=new ArrayList<> ();
                      proxyList.add(proxy);
                      targetMap.put(targetClass,proxyList);
                  }
              }
          }
          return targetMap;
      }	
      
  ```

- 最后在AopHelper中通过一个静态块来初始化整个AOP框架

  ```java
      static {
          try{
              Map<Class<?>,Set<Class<?>>> proxyMap=createProxyMap();
              Map<Class<?>,List<Proxy>>  targetMap=createTargetMap(proxyMap);
              for(Map.Entry<Class<?>,List<Proxy>> targetEntry: targetMap.entrySet()){
                  Class<?> targetClass=targetEntry.getKey();
                  List<Proxy> proxyList=targetEntry.getValue();
                  Object proxy= ProxyManager.createProxy(targetClass,proxyList);
                  BeanHelper.setBean(targetClass,proxy);
              }
          }catch (Exception e){
              LOGGER.error("aop failure",e);
          }
      }
  ```

  **获取代理类及目标类集合的映射关系,进一步获取目标类与代理对象列表的映射关系,进而遍历这个映射关系,从中获取目标类与代理对象列表,调用ProxyManager.createProxy方法获取代理对象,调用BeanHelper.setBean方法,将该代理对象重新放入Bean Map中**

- 最后将AopHelper添加到HelperLoader中进行初始化