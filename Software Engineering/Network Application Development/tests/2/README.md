# Рубежный контроль №2 (NP ITMO 2025-autumn)

> <p align="center">
>     <img src="https://media1.tenor.com/m/r6zw1Ij72yQAAAAC/dandadan-dandadan-anime.gif" alt="actual crashout" width="500"/>
> </p>

> [!TIP]
> Можно так-то не сдавать их, если уверены, что закроете предмет на 60 баллов.

#### 1. Каким из следующих методов можно создать React-компонент:

- [ ] ```class Welcome extends React.Component { return <h1>Hello World</h1>; }```
- [x] ```const Welcome = () => { return <h1>Hello World</h1>; }```
- [x] ```function Welcome(props) { return <h1>Hello World</h1>; }```

#### 2. Какие методы жизненного цикла должны быть определены в классовом компоненте?

- [ ] destructor
- [ ] constructor
- [ ] componentDidMount
- [x] render
- [ ] getDerivedStateFromProps

#### 3. Какие методы жизненного цикла должны быть определены в функциональном компоненте?

- [x] Ни один из перечисленных
- [ ] constructor
- [ ] render
- [ ] destructor
- [ ] getDerivedStateFromProps

#### 4. В каком методе жизненного цикла компонента класса стоит выполнять асинхронные запросы?

- [ ] componentWillUnmount
- [ ] render
- [x] componentDidMount
- [ ] constructor

#### 5. Какие значения будут в переменных состояния?

```javascript
this.state = {car: "ferrari"}

function onClick() {
    setState({car: "bmw"});
    //(1)
    this.state.car = "mercedes"
    //(2)
}

//3 After the handler completes
```

- [ ] ```(1): 'ferrari'; (2): 'ferrari'; (3): 'bmw';```
- [x] ```(1): 'ferrari'; (2): 'mercedes'; (3): 'bmw';```
- [ ] ```(1): 'ferrari'; (2): 'bmw'; (3): 'mercedes';```
- [ ] ```(1): 'bmw'; (2): 'mercedes'; (3): 'mercedes';```

#### 6. Какие методы классового компонента выполняются на этапе обновления?

- [ ] ```constructor()```
- [x] ```static getDerivedStateFromProps()```
- [x] ```render()```
- [x] ```getSnapshotBeforeUpdate()```
- [x] ```componentDidUpdate()```
- [ ] ```componentWillUnmount()```

#### 7. Что вызывает метод render() классового компонента?

- [x] Обновление родительского компонента
- [x] Изменение props текущего компонента
- [x] Изменение state текущего компонента
- [ ] Обновление дочернего компонента

#### 8. Для чего можно использовать метод `componentWillUnmount()`?

- [ ] Обновление props
- [x] Отмена асинхронных вызовов
- [x] Очистка таймеров
- [ ] Обновление state

#### 9. Какое использование промиса вызовет exception в приложении?

- [ ] ```async function foo() { return "Hello!" }```
- [ ] ```Promise.resolve("Hello!")```
- [ ] ```new Promise((resolve, reject) => { console.log("Hello!"); })```
- [ ] Ни одно из перечисленных
- [x] ```Promise.reject("Hello!")```
- [ ] ```new Promise(resolve => setTimeout(() => { console.log("Hello!"); })```

#### 10. Что будет напечатано на экране?

```javascript
await Promise.resolve(() => console.log("A"));
Promise.resolve(console.log("B"));
Promise.resolve(() => console.log("C"));
await Promise.resolve(console.log("D"));
console.log("E")
```

- [ ] C - D - E
- [ ] A - B - C - D - E
- [x] B - D - E
- [ ] A - D - B - C - E

#### 11. Что будет напечатано на экране?

```javascript
Promise.resolve(() => "Hello!").then((val) => console.log(val))
```

- [x] ```f () { return "Hello!" }```
- [ ] ```f () { return console.log("Hello!"); } ```
- [ ] Ничего не будет напечатано
- [ ] Hello!

#### 12. Каким из способов можно создать таймер, который будет выводить "Hello!" на консоль каждую секунду?

- [ ] ```new Promise(() => setInterval(() => { console.log("Hello!")}, 1000))```
- [ ] ```setTimeout(() => { console.log("Hello!")}, 1000)```
- [ ] ```new Promise(() => setTimeout(() => {console.log("Hello!")}, 1000))```
- [x] ```setInterval(() => { console.log("Hello!")}, 1000)```

#### 13. Какие из способов создания React-компонента правильные

- [ ] ```export function applyButton() { return (<h1>Hi everyone, I'm FunComponent</h1>) };```
- [ ] ```export const applyButton = () => { return (<h1>Hi everyone, I'm FunComponent</h1>) };```
- [x] ```export function ApplyButton() { return (<h1>Hi everyone, I'm FunComponent</h1>) };```
- [ ] ```export function ApplyButton() => <h1>Hi everyone, I'm FunComponent</h1>```
- [ ] ```export function ApplyButton() => { <h1>Hi everyone, I'm FunComponent</h1> }```
- [ ] ```export const applyButton = () => <h1>Hi everyone, I'm FunComponent</h1>```

#### 14. Какие из способов создания React-компонента правильные?

- [ ] 
  ```class ApplyButton extends React.Component { constructor(props:any) {super(props)}; render() {return <h1>Hello!</h1>}}```
- [ ] 
  ```class ApplyButton extends React.Component { constructor(props:any) {super(props); this.setState({})}; render() {return <h1>Hello!</h1>}}```
- [x] 
  ```class ApplyButton extends React.Component { constructor(props:any) {this.state = {}}; render() {return <h1>Hello!</h1>}}```
- [ ] 
  ```class ApplyButton extends React.Component { constructor(props:any) {this.state = {}; super(props)}; render() {return <h1>Hello!</h1>}}```
- [ ] ```class ApplyButton extends React.Component { constructor(props:any) { return <h1>Hello!</h1> }```
- [ ] ```class ApplyButton extends React.Component { constructor(props:any) { render() {return <h1>Hello!</h1> }}```

#### 15. Какой `hook` в функциональном компоненте можно использовать для замены метода жизненного цикла

`componentDidUpdate`?

- [ ] `useState`
- [ ] `useRef`
- [x] `useEffect`
- [ ] `useContext`

#### 16. fetch получил HTTP-статус 404. Что произойдёт?

- [ ] Promise будет rejected
- [ ] Произойдет исключение
- [x] catch не выполнится
- [ ] fetch автоматически повторит запрос

#### 17. Есть структура описывающая модель пользователя на фронте:

```kotlin
interface User {
  id: number;
  name: string;
}
```

#### С сервера приходит данные следующего вида:

```json
{
  "id": "10",
  "name": 123
}
```

- [x] Ошибки не будет
- [ ] Нет правильного ответа
- [ ] TypeScript выбросит ошибку во время выполнения
- [ ] Ошибка только на этапе компиляции
- [ ] JavaScript приведёт тип автоматически

#### 18. В dev React 18 запрос уходит дважды.

```javascript 
useEffect(() => fetchData(), [])
```

#### Что из перечисленного является причиной?

- [ ] React баг
- [x] Эмуляция mount/unmount
- [ ] Нарушение идемпотентности
- [x] StrictMode

#### 19. Что из этого НЕ является целью CORS?

- [ ] Ограничение браузера
- [ ] Защита пользователя
- [x] user-agent
- [ ] Защита сервера

#### 20. Cookie с флагами:

```
HttpOnly; Secure; SameSite=None
```

#### От чего она НЕ защищена?

- [x] Session Fixation
- [ ] XSS
- [ ] CSRF
- [ ] MITM

#### 21. Почему это логически неверно?

```javascript
setCount(count + 1);
setCount(count + 1);
```

- [x] Замыкание
- [ ] Батчинг
- [ ] Асинхронно

#### 22. Что принципиально невозможно сделать через fetch?

- [ ] Работать с Cookies
- [ ] Отправить FormData
- [x] Отследить прогресс загрузки
- [ ] Отменить запрос

#### 23. Почему такой код опасен?

```javascript
useEffect(() => {
    fetchUser(id).then(setUser);
}, [id]);
```

- [x] Нет abort
- [x] Возможна race condition
- [x] setUser после unmount
