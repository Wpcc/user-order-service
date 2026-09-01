这段代码的目标是：把项目中抛出的异常，统一转换成前端能读懂的 JSON，而不是返回默认报错页面。
整体流程：
Controller 接收请求
→ DTO 校验或 Service 业务处理
→ 出现异常
→ GlobalExceptionHandler 自动接住异常
→ 返回统一 JSON 错误响应
你不需要手动调用 GlobalExceptionHandler 里的方法，Spring 会自动调用。
@RestControllerAdvice
public class GlobalExceptionHandler {
@RestControllerAdvice 表示“全局 Controller 异常处理器”。

- RestController：返回 JSON。
- Advice：对所有 Controller 生效。
- GlobalExceptionHandler：类名，意思是全局异常处理器。
  第一种：处理 DTO 校验失败。
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiErrorResponse> handleValidationException(
  MethodArgumentNotValidException exception
  ) {
  ExceptionHandler(...) 的含义是：
  如果项目中出现 MethodArgumentNotValidException
  → 调用 handleValidationException 方法
  这个异常通常由 @Valid 触发。例如：
  public record CreateUserRequest(
  @NotBlank(message = "用户名不能为空")
  String username
  ) {
  }
  前端传：
  {
  "username": ""
  }
  Spring 就会抛出 MethodArgumentNotValidException。
  下面这段是取出所有字段的错误信息：
  Map<String, String> fieldErrors = exception.getBindingResult()
  .getFieldErrors()
  .stream()
  .collect(Collectors.toMap(
  fieldError -> Objects.requireNonNull(fieldError).getField(),
  fieldError -> Objects.requireNonNullElse(
  Objects.requireNonNull(fieldError).getDefaultMessage(),
  "请求参数不合法"
  ),
  (first, ignored) -> first
  ));
  拆开理解：
  exception.getBindingResult().getFieldErrors()
  得到一个字段错误列表，例如：
  username → 用户名不能为空
  quantity → 数量必须大于 0
  .stream()
  把这个列表变成流，以便逐条处理。
  fieldError -> Objects.requireNonNull(fieldError).getField()
  指定 Map 的 key，也就是字段名：
  username
  quantity
  fieldError -> ...getDefaultMessage()
  指定 Map 的 value，也就是你在校验注解里写的错误提示：
  用户名不能为空
  数量必须大于 0
  (first, ignored) -> first
  如果同一个字段有两条校验错误，保留第一条。
  最终得到：
  {
  "username": "用户名不能为空",
  "quantity": "数量必须大于 0"
  }
  Objects.requireNonNull(...) 和 Objects.requireNonNullElse(...) 是为了处理 VS Code 的空值安全提示；正常情况下字段错误本身不会是 null。
  接下来创建响应对象：
  ApiErrorResponse response = new ApiErrorResponse(
  HttpStatus.BAD_REQUEST.value(),
  "请求参数校验失败",
  fieldErrors
  );
  等价于：
  new ApiErrorResponse(
  400,
  "请求参数校验失败",
  {
  "username": "用户名不能为空"
  }
  );
  最后：
  return ResponseEntity.badRequest().body(response);
  表示 HTTP 状态码返回 400 Bad Request，响应体返回 response，Spring 会自动转成 JSON：
  {
  "status": 400,
  "message": "请求参数校验失败",
  "fieldErrors": {
  "username": "用户名不能为空"
  }
  }
  第二种：处理业务代码主动抛出的非法参数异常。
  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ApiErrorResponse> handleIllegalArgumentException(
  IllegalArgumentException exception
  ) {
  例如 Service 中：
  throw new IllegalArgumentException("商品不存在：1");
  Spring 会自动调用这个方法。
  ApiErrorResponse response = new ApiErrorResponse(
  HttpStatus.BAD_REQUEST.value(),
  Objects.requireNonNullElse(exception.getMessage(), "请求参数不合法"),
  Map.of()
  );
  这里：
- 400：请求参数或业务条件不合法。
- exception.getMessage()：取出你抛异常时写的文字，例如“商品不存在：1”。
- Map.of()：创建一个空 Map，因为这不是 DTO 字段校验，没有具体字段错误。
  最终响应：
  {
  "status": 400,
  "message": "商品不存在：1",
  "fieldErrors": {}
  }
  核心分工：
  DTO 格式、字段校验失败
  → MethodArgumentNotValidException
  → 返回 fieldErrors

用户不存在、商品不存在、库存不足等业务失败
→ IllegalArgumentException
→ 返回 message，fieldErrors 为空
