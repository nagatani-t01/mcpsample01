package org.springframework.samples.petclinic.mcp.privacy;

import org.springframework.samples.petclinic.mcp.client.PetclinicApiException;

/**
 * 個人情報を含む応答を返す前に、ユーザー同意が必要なときに送出する。
 * メッセージに実データは含めない。
 */
public class PersonalDataConsentRequiredException extends PetclinicApiException {

    public static final String MESSAGE =
        "この応答には個人情報（氏名・住所・電話番号など）が含まれます。"
            + "ユーザーに取得・表示への同意を確認し、同意が得られたら grant_personal_data_consent を実行してから、"
            + "同じツールをもう一度呼び出してください。同意が無い場合は個人情報を返さず、要約もしないでください。";

    public PersonalDataConsentRequiredException() {
        super(MESSAGE);
    }
}
