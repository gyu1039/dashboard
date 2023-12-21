console.log('1. =========== spread');
const a = [1, 2, 3];
const b = [...a];

b.push(4);

console.log(`a의 값은: ${a}`);
console.log(`b의 값은: ${b}`);

console.log('2. =========== concat'); // add할 때
const a2 = [1, 2, 3];
const b2 = a2.concat(4);

console.log(`a2의 값은: ${a2}`);
console.log(`b2의 값은: ${b2}`);

const c2 = [0, ...a, 4];
console.log(`c2의 값은: ${c2}`);

console.log('3. =========== filter');
const a3 = [1, 2, 3];
const b3 = a3.filter((n) => {
  return n !== 1;
});

console.log(`a3의 값은: ${a3}`);
console.log(`b3의 값은: ${b3}`);

console.log('4. =========== slice'); // 삭제할 때
const a4 = [1, 2, 3];
const b4 = a4.slice(0, 2);

console.log(`a4의 값은: ${a4}`);
console.log(b4);

const c4 = [...a4.slice(0, 2), 4, ...a4.slice(2, 3)];
console.log(c4);

console.log('5. =========== map'); // 삭제할 때
const a5 = [1, 2, 3];

// a5.forEach((n) => {console.log(n)});

const b5 = a5.map((n) => n * 2);
console.log(a5);
console.log(b5);

console.log('========================');

const a6 = { id: 1, name: '홍길동' };
const b6 = { ...a6, name: '임꺽정' };
console.log(a6);
console.log(b6);

const users = [
  { id: 1, name: '구태모', phone: '2222' },
  { id: 2, name: '이대엽', phone: '3333' },
  { id: 3, name: '오승훈', phone: '4444' },
];

const updateUserDTO = {
  id: 2,
  name: '홍길동',
};

const newUsers = users.map((user) => {
  return user.id === updateUserDTO.id ? { ...user, ...updateUserDTO } : user;
});
console.log(newUsers);
