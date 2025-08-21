pub struct Rc4 {
    i: usize,
    j: usize,
    state: Vec<u8>,
}
pub fn rc4(key: Vec<u8>) {
    let mut ksa_rc4: Rc4 = Rc4::ksa(key);
    print!("Элементы массива S после перестановки: ");
    for (i, &element) in ksa_rc4.state.iter().enumerate() {
        print!("{}", element);
        if i < ksa_rc4.state.len() - 1 {
            print!(",");
        }
    }
    let mut lookup_results: Vec<u8> = Vec::new();
    print!("\nПервые 5 значений потока ключа K: ");
    for _ in 0..5 {
        lookup_results.push(ksa_rc4.lookup());
    }
    lookup_results.reverse();
    for (i, &e) in lookup_results.iter().enumerate() {
        print!("{}", e);
        if i < ksa_rc4.state.len() - 1 {
            print!(",");
        }
    }
}

impl Rc4 {
    pub fn ksa(key: Vec<u8>) -> Rc4 {
        assert!(key.len() >= 1 && key.len() <= 256);
        assert!(!key.is_empty());

        let key_len: usize = key.len();
        let mut state: Vec<u8> = (0..key_len).map(|x| x as u8).collect();

        let mut j: usize = 0;
        for i in 0..key_len {
            j = (j + state[i] as usize + key[i % key_len] as usize) % key_len;
            state.swap(i, j);
        }
        Rc4 { i: 0, j: 0, state }
    }

    pub fn lookup(&mut self) -> u8 {
        let n = self.state.len();
        self.i = (self.i + 1) % n;
        self.j = (self.j + self.state[self.i] as usize) % n;
        self.state.swap(self.i, self.j);
        let t = (self.state[self.i] as usize + self.state[self.j] as usize) % n;
        self.state[t]
    }

    pub fn keystream(&mut self, out: &mut [u8]) {
        for k in out.iter_mut() {
            *k = self.lookup();
        }
    }

    pub fn process(&mut self, data: &mut [u8]) {
        for byte in data.iter_mut() {
            *byte ^= self.lookup();
        }
    }
}
