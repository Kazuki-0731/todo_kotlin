# TODOアプリ
Kotlinで制作したTODOアプリです

## [アプリアイコン]
<img src="https://user-images.githubusercontent.com/28224709/68698113-8e287880-05c3-11ea-9e27-1058ecec00b8.jpg" width="50%">

## [機能説明]
* 画面全体をリスト表示しています。
* アプリ起動後、DBに保存されているデータが表示されます。
* 初期データは空です。
* 表示する順序は、データを挿入された時の順番になります。
* データの追加は、右下のアイコン押下でTODO追加します。

## [主な仕様]
* TODOリストに登録可能な最大件数
  * 100件
* 最大入力文字数
  * 50文字
* 入力できる有効文字列
  * 全角半角英字
  * 全角半角数字
  * 全角半角日本語(Shift-JIS)
  * 記号
  * スペース
* 入力できない文字列
  * 空文字(0桁の文字列)
* アラート
  * 最大件数を超える場合、「最大登録件数(100)を超えるため、１件以上削除してください」と表示
  * 最大文字数を超える場合、「最大文字数を超えるため、50文字以下で入力してください」と表示
  * ゴミ箱アイコンを押下で、「削除してもよろしいでしょうか？」と表示
* テキストボックス動作
  * 改行を入力できない
    * スマホのキーボードでEnterを押下できない

## [プロジェクト構成]
Androidプロジェクトのフォルダ構成は、MVCアーキテクチャになっております。

Controllerに関しては、画面遷移が特にないため、MainActivityのみとなります。

### Model構成
* action
  * 設定系
* database
  * SQLiteのデータベース周り
* entities
  * キャッシュ(一時保存)

![Model](https://user-images.githubusercontent.com/28224709/68700424-3d674e80-05c8-11ea-9679-a672599165a6.png)

### View構成
* common
  * フロントに共通化する処理や定数など
* fragment
  * fragment関係
* util
  * UIオブジェクトのカスタムクラス郡

![Veiw](https://user-images.githubusercontent.com/28224709/68700445-448e5c80-05c8-11ea-85f4-50bb43d75bb0.png)

## [各アイコンの説明]
* 右下のアイコン押下でダイアログ表示
  * ダイアログ内にあるテキストボックス入力でDB内にデータ挿入
  * キャンセルでダイアログを閉じる
* 中央のアイコン押下で全表示/Active/Inactiveを切り替えます
  * 全表示の場合、三本線のアイコンになります
    * 全タスク表示されます
  * Activeの場合、黄色い星アイコンになります
    * 完了タスクのみ表示されます
  * Inactiveの場合、グレーの星アイコンになります
    * 未完了タスクのみ表示されます
* リストの右側のゴミ箱アイコン押下で１行削除します
  * 削除されると画面がリロードします

## [実際の画面]
### 入力画面
<img src="https://user-images.githubusercontent.com/28224709/68698753-f592f800-05c4-11ea-93c8-96a16ff58b19.jpg" width="50%">

### データ挿入後
<img src="https://user-images.githubusercontent.com/28224709/68697851-0478ab00-05c3-11ea-82ff-dd0e1b969001.jpg" width="50%">

### 未完了タスクのみ表示(中央のボタン押下)
<img src="https://user-images.githubusercontent.com/28224709/68697825-f88ce900-05c2-11ea-90f7-bdd208a15fa0.jpg" width="50%">

### 完了タスクのみ表示(中央のボタン押下)
<img src="https://user-images.githubusercontent.com/28224709/68697814-f1fe7180-05c2-11ea-8c3a-daee6f8e37a3.jpg" width="50%">

### すべてのタスク表示(中央のボタン押下)
<img src="https://user-images.githubusercontent.com/28224709/68697851-0478ab00-05c3-11ea-82ff-dd0e1b969001.jpg" width="50%">

### 最大入力文字数を超えた場合
<img src="https://user-images.githubusercontent.com/28224709/68698720-e318be80-05c4-11ea-84c0-8a58afd0284c.jpg" width="50%">

### TODOリストに登録可能な最大件数を超えた場合
<img src="https://user-images.githubusercontent.com/28224709/68698741-f0ce4400-05c4-11ea-97c0-d6657a955e30.jpg" width="50%">

### 対象のTODO項目を削除する場合
<img src="https://user-images.githubusercontent.com/28224709/68697786-e448ec00-05c2-11ea-8231-54c4724b06e5.jpg" width="50%">

### 0桁で入力した場合
<img src="https://user-images.githubusercontent.com/28224709/68699307-1e67bd00-05c6-11ea-8e90-432f0fef0ebf.jpg" width="50%">

## [開発時に利用できるツールについて]
### Stetho
実行中のアプリのSQLite 内容を確認するためにFacebook が開発した[Stetho](https://facebook.github.io/stetho/) を導入しています。
下記の手順で中身を確認できます。

1. Debug ビルドでアプリをインストールし、アプリを起動状態にする
2. 手順１でインストールしたデバイスとPC をケーブルなどで接続する(エミュレータの場合は必要ないです)
3. PC のGoogle Chrome を起動し[chrome://inspect/#devices](chrome://inspect/#devices) を表示する
4. PC が正常にデバイスを認識できている場合、手順３で表示されたページでアプリ名が表示されるので、それを選択する
5. アプリの開発者ツールが表示されるので"Resources" の"Web SQL" を選択し、DB の中身を確認する
