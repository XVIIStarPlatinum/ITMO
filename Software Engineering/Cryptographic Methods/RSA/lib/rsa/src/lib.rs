use std::collections::HashMap;
use modpow::modpow;
use num::bigint::{BigInt, BigUint};
use once_cell::sync::Lazy;

const CYR_DICTIONARY: Lazy<HashMap<char, u8>> = Lazy::new(|| HashMap::from([
    ('А', 192), ('Б', 193), ('В', 194), ('Г', 195), ('Д', 196), ('Е', 197), ('Ж', 198), ('З', 199),
    ('И', 200), ('Й', 201), ('К', 202), ('Л', 203), ('М', 204), ('Н', 205), ('О', 206), ('П', 207),
    ('Р', 208), ('С', 209), ('Т', 210), ('У', 211), ('Ф', 212), ('Х', 213), ('Ц', 214), ('Ч', 215),
    ('Ш', 216), ('Щ', 217), ('Ъ', 218), ('Ы', 219), ('Ь', 220), ('Э', 221), ('Ю', 222), ('Я', 223),
    ('а', 224), ('б', 225), ('в', 226), ('г', 227), ('д', 228), ('е', 229), ('ж', 230), ('з', 231),
    ('и', 232), ('й', 233), ('к', 234), ('л', 235), ('м', 236), ('н', 237), ('о', 238), ('п', 239),
    ('р', 240), ('с', 241), ('т', 242), ('у', 243), ('ф', 244), ('х', 245), ('ц', 246), ('ч', 247),
    ('ш', 248), ('щ', 249), ('ъ', 250), ('ы', 251), ('ь', 252), ('э', 253), ('ю', 254), ('я', 255)
]));
const CYR_DICTIONARY_INVERTED: Lazy<HashMap<u8, char>> = Lazy::new(||CYR_DICTIONARY.iter()
    .map(|(k, v)| (*v, *k)).collect());

pub fn find_private_key(p: u64, q: u64, c: u64) -> [u64; 2] {
    let n = p * q;
    let phi_n = (p - 1) * (q - 1);
    let d = find_private_exponent(c, phi_n);
    [d.unwrap_or(0), n]
}

pub fn generate_dictionary_block(msg: String) -> Vec<u64> {
    let mut block: Vec<u64> = Vec::new();
    for c in msg.chars() {
        block.push(*CYR_DICTIONARY.get(&c).unwrap() as u64);
    }
    block
}

pub fn encrypt(msg: Vec<u64>, e: u64, n: u64) -> String {
    msg.into_iter()
        .map(|m| {
            let c: BigInt = modpow(&BigUint::from(m), &BigUint::from(e), &BigUint::from(n));
            let s = c.to_string();
            if s.len() >= 4 { s } else { format!("{:0>4}", s) }
        })
        .collect::<String>()
}

pub fn decrypt(encrypted: String, private_key: Vec<u64>) -> String {
    let chars: Vec<char> = encrypted.chars().collect();
    let enc_vec: Vec<u64> = chars.chunks(4)
        .map(|chunk| chunk.iter().collect::<String>())
        .map(|s| s.parse::<u64>().unwrap()).collect::<Vec<u64>>();
    let mut msg: String = "".to_string();
    for c in enc_vec {
        let m: BigInt = modpow(&BigUint::from(c), &BigUint::from(private_key[0]), &BigUint::from(private_key[1]));
        msg.push(CYR_DICTIONARY_INVERTED[&m.to_string().parse::<u8>().unwrap()]);
    }
    msg
}

fn find_private_exponent(e: u64, phi_n: u64) -> Option<u64> {
    let mut r0 = phi_n as i128;
    let mut r1 = e as i128;
    let mut t0 = 0i128;
    let mut t1 = 1i128;

    while r1 != 0 {
        let q = r0 / r1;
        let r2 = r0 - q * r1;
        let t2 = t0 - q * t1;

        r0 = r1;
        r1 = r2;
        t0 = t1;
        t1 = t2;
    }

    if r0 != 1 {
        return None;
    }

    let inverse = if t0 < 0 {
        t0 + phi_n as i128
    } else {
        t0
    };
    Some(inverse as u64)
}
