/**
 * main.js (JavaのBlackjack.javaの役割)
 */
let deck, player, dealer;
let canPeek = true;

// ゲームの初期化
function initGame() {
    deck = new Deck();
    deck.shuffle();
    player = new Player("Player");
    dealer = new Player("Dealer");
    canPeek = true;

    // 初期配布
    player.addCard(deck.draw());
    player.addCard(deck.draw());
    dealer.addCard(deck.draw());
    dealer.addCard(deck.draw());

    updateDisplay(false); // 画面を更新
}

// ヒットボタンを押した時
function hit() {
    player.addCard(deck.draw());
    if (player.calculateScore() > 21) {
        updateDisplay(true);
        alert("バースト！あなたの負けです。");
    } else {
        updateDisplay(false);
    }
}

// スタンドボタンを押した時
function stand() {
    while (dealer.calculateScore() < 17) {
        dealer.addCard(deck.draw());
    }
    updateDisplay(true);
    judge();
}

// 透視スキルボタンを押した時
function peek() {
    if (canPeek) {
        const secretCard = dealer.hand[0];
        alert("【スキル発動】ディーラーの伏せカードは " + secretCard.suit + secretCard.rank + " です！");
        canPeek = false;
        // スキルボタンを無効化する処理などをここに追加できる
    }
}

// 勝敗判定
function judge() {
    const pScore = player.calculateScore();
    const dScore = dealer.calculateScore();
    let message = `あなた: ${pScore}, ディーラー: ${dScore}\n`;

    if (dScore > 21 || pScore > dScore) {
        message += "あなたの勝ち！";
    } else if (pScore < dScore) {
        message += "ディーラーの勝ち...";
    } else {
        message += "引き分け！";
    }
    alert(message);
}

// 画面を書き換える関数（HTMLとの繋ぎ込み）
function updateDisplay(isFinished) {
    // 自分の手札を表示
    document.getElementById("player-hand").innerText = player.hand.join(", ");
    document.getElementById("player-score").innerText = player.calculateScore();

    // ディーラーの手札を表示（終了前は1枚隠す）
    if (isFinished) {
        document.getElementById("dealer-hand").innerText = dealer.hand.join(", ");
        document.getElementById("dealer-score").innerText = dealer.calculateScore();
    } else {
        document.getElementById("dealer-hand").innerText = "???, " + dealer.hand[1];
        document.getElementById("dealer-score").innerText = "?";
    }
}

// ページを読み込んだらゲーム開始
window.onload = initGame;