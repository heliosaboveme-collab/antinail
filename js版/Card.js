/**
 * Cardクラス (JavaのCard.javaの移植版)
 */
class Card {
    constructor(suit, rank) {
        this.suit = suit; // マーク (♥, ♦, ♠, ♣)
        this.rank = rank; // 数字 (A, 2-10, J, Q, K)
    }

    // JavaのgetPointメソッドと同じ役割
    getPoint() {
        if (this.rank === "A") return 11;
        if (["J", "Q", "K"].includes(this.rank)) return 10;
        return parseInt(this.rank);
    }

    // 画面に表示するための名前を返す
    toString() {
        return `${this.suit} ${this.rank}`;
    }
}